package com.synapse.api_gateway_server.ratelimit.limit;

import java.time.Instant;
import java.util.List;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import com.synapse.api_gateway_server.dto.RateLimitDecision;
import com.synapse.api_gateway_server.dto.RateLimitPolicy;
import com.synapse.api_gateway_server.dto.RateLimitResponse;
import com.synapse.api_gateway_server.exception.ExceptionType;
import com.synapse.api_gateway_server.exception.GlobalRateLimitException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class CustomRedisRateLimiter implements RateLimiter {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisScript<List<Long>> script;

    @Override
    public Mono<RateLimitResponse> check(String id, RateLimitPolicy policy) {
        final List<String> keys = buildKeys(id);

        final List<String> args = List.of(
            String.valueOf(policy.replenishRate()),
            String.valueOf(policy.burstCapacity()),
            String.valueOf(Instant.now().getEpochSecond()),
            String.valueOf(policy.requestedTokens()),
            String.valueOf(policy.maxTotalRequests()),
            String.valueOf(policy.totalRequestsTtlSeconds())
        );

        return redisTemplate.execute(script, keys, args)
                .next() // 이 스크립트는 단 하나의 결과만 필요해 -> 공식문서와는 다르게 하나의 결과를 반환하도록 lua 스크립트를 작성하였기에 reduce 는 과함.. 결과를 반환해도 이 값이 음수인지에 따라 판단이 가능함 (물론 filter 단에서)
                .map(results -> {
                    long decisionCode = results.get(0);
                    long tokensLeft = results.get(1) != null ? results.get(1) : 0L;
                    long totalRequests = results.get(2) != null ? results.get(2) : 0L;

                    RateLimitDecision decision = RateLimitDecision.fromCode(decisionCode);

                    return RateLimitResponse.from(
                        tokensLeft,
                        totalRequests,
                        policy,
                        decision);
                })
                .onErrorResume(throwable -> {
                    log.error("Rate limit check failed for key {}: {}", keys, throwable.getMessage(), throwable);
                    return Mono.error(new GlobalRateLimitException(
                        ExceptionType.RATE_LIMIT_CHECK_FAILED, throwable.getMessage(),throwable)
                    );
                });
    }

    private List<String> buildKeys(String id) {
        String prefix = "ratelimit:{" + id + "}"; // redis cluster를 사용하고 있다면 동일한 해시 슬롯에 할당하기 위해 {} 필요함

        String tokensKey = prefix + ":tokens";
        String timestampKey = prefix + ":timestamp";
        String totalRequestsKey = prefix + ":total_requests";

        return List.of(tokensKey, timestampKey, totalRequestsKey);
    }
    
}
