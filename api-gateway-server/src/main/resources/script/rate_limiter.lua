-- 복제 제어 설정
redis.replicate_commands()

-- 키 정의
local tokens_key = KEYS[1]
local timestamp_key = KEYS[2]
local total_requests_key = KEYS[3]  -- 총 요청 횟수를 저장할 키

-- 파라미터 파싱
local rate = tonumber(ARGV[1])
local capacity = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])
local max_requests = tonumber(ARGV[5])  -- 구독자별 최대 요청 횟수
local total_requests_ttl = tonumber(ARGV[6])

-- 현재 총 요청 횟수 조회
local total_requests = tonumber(redis.call("get", total_requests_key) or 0)

-- 최대 요청 횟수 초과 체크
if total_requests >= max_requests then
    return {0, -1, total_requests}  -- 거부
end

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

-- 토큰 보충 계산
local delta = math.max(0, now - last_refreshed)
local filled_tokens = math.min(capacity, last_tokens + (delta * rate))

-- 요청 처리
local allowed = filled_tokens >= requested
local new_tokens = filled_tokens

if allowed then
  new_tokens = filled_tokens - requested
  total_requests = redis.call("INCR", total_requests_key)
  redis.log(redis.LOG_DEBUG, string.format(
    "Request allowed - new_tokens: %s, requested: %s",
    new_tokens, requested
  ))
else
  redis.log(redis.LOG_DEBUG, string.format(
    "Request denied - available: %s, requested: %s",
    filled_tokens, requested
  ))
end

local token_bucket_ttl = math.ceil(capacity / rate) * 2

redis.call("SET", tokens_key, tokens_left, "EX", token_bucket_ttl)
redis.call("SET", timestamp_key, now, "EX", token_bucket_ttl)

if total_requests == 1 then
  redis.call("EXPIRE", total_requests_key, total_requests_ttl)
end

-- 결과 반환
return { allowed and 1 or 0, new_tokens, total_requests }
