redis.replicate_commands()

-- 키 정의
local tokens_key = KEYS[1]
local timestamp_key = KEYS[2]
local total_requests_key = KEYS[3]  -- 총 요청 횟수를 저장할 키

-- 의사 결정 코드 정의
local DECISION_RATE_LIMIT = 0
local DECISION_ALLOWED = 1
local DECISION_MAX_TOTAL = 2

-- 파라미터 파싱
local rate = tonumber(ARGV[1]) or 0
local capacity = tonumber(ARGV[2]) or 0
local now = tonumber(ARGV[3])
local requested = math.max(tonumber(ARGV[4]) or 0, 0)
local max_requests = tonumber(ARGV[5]) or 0  -- 구독자별 최대 요청 횟수
local total_requests_ttl = tonumber(ARGV[6]) or 0

local total_requests = tonumber(redis.call("get", total_requests_key) or 0)

-- 현재 시간 처리 (Redis 시스템 시간 폴백)
if now == nil then
  now = redis.call('TIME')[1]
end

-- 디버깅 로깅
redis.log(redis.LOG_DEBUG, string.format(
  "Rate Limiter - rate: %s, capacity: %s, now: %s, requested: %s, max_requests: %s",
  rate, capacity, now, requested, max_requests
))

-- 현재 토큰 수 조회
local last_tokens = tonumber(redis.call("get", tokens_key) or capacity)

-- 마지막 업데이트 시간 조회
local last_refreshed = tonumber(redis.call("get", timestamp_key) or 0)

-- 입력값 방어: rate/capacity/max_requests는 양수여야 정상 동작
if rate <= 0 or capacity <= 0 or max_requests <= 0 then
  redis.log(redis.LOG_WARNING, "Rate limiter: invalid configuration encountered")
  return {DECISION_MAX_TOTAL, last_tokens, total_requests}
end

-- 토큰 보충 계산
local delta = math.max(0, now - last_refreshed)
local filled_tokens = math.min(capacity, last_tokens + (delta * rate))
local new_tokens = filled_tokens
local token_bucket_ttl = math.max(1, math.ceil((capacity / rate))) * 2

-- 총량 제한을 먼저 확인하여 불필요한 토큰 소비 방지
if total_requests >= max_requests then
  redis.call("SET", tokens_key, new_tokens, "EX", token_bucket_ttl)
  redis.call("SET", timestamp_key, now, "EX", token_bucket_ttl)
  return {DECISION_MAX_TOTAL, new_tokens, total_requests}
end

-- 요청 처리
local allowed = filled_tokens >= requested
local decision = DECISION_ALLOWED

if allowed then
  new_tokens = filled_tokens - requested
  total_requests = redis.call("INCR", total_requests_key)
  redis.log(redis.LOG_DEBUG, string.format(
    "Request allowed - new_tokens: %s, requested: %s",
    new_tokens, requested
  ))
  if total_requests == 1 and total_requests_ttl > 0 then
    redis.call("EXPIRE", total_requests_key, total_requests_ttl)
  end
else
  decision = DECISION_RATE_LIMIT
  redis.log(redis.LOG_DEBUG, string.format(
    "Request denied - available: %s, requested: %s",
    filled_tokens, requested
  ))
end

redis.call("SET", tokens_key, new_tokens, "EX", token_bucket_ttl)
redis.call("SET", timestamp_key, now, "EX", token_bucket_ttl)

-- 결과 반환
return { decision, new_tokens, total_requests }
