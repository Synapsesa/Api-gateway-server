package com.synapse.api_gateway_server.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.web.server.ServerWebExchange;

import com.synapse.api_gateway_server.dto.RateLimitPolicy;
import com.synapse.api_gateway_server.exception.ExceptionType;
import com.synapse.api_gateway_server.exception.RateLimitExceededException;
import com.synapse.api_gateway_server.ratelimit.limit.RateLimiter;

import reactor.core.publisher.Mono;

public abstract class AbstractRateLimiterGatewayFilterFactory<C> extends AbstractGatewayFilterFactory<C> {
    private final RateLimiter rateLimiter;

    public AbstractRateLimiterGatewayFilterFactory(RateLimiter rateLimiter, Class<C> configClass) {
        super(configClass);
        this.rateLimiter = rateLimiter;
    }

    protected Mono<Void> checkRateLimit(
            String key,
            RateLimitPolicy policy,
            ServerWebExchange exchange,
            GatewayFilterChain chain,
            ExceptionType rateLimitExceptionType,
            ExceptionType totalLimitExceptionType) {
        return rateLimiter.check(key, policy)
                .flatMap(response -> {
                    exchange.getResponse().getHeaders().add(
                        "X-RateLimit-Remaining",
                        String.valueOf(response.tokensLeft()));
                    exchange.getResponse().getHeaders().add(
                        "X-RateLimit-Burst-Capacity",
                        String.valueOf(response.policy().burstCapacity()));

                    return switch (response.decision()) {
                        case ALLOWED -> chain.filter(exchange);
                        case DENIED_MAX_TOTAL -> Mono.error(new RateLimitExceededException(totalLimitExceptionType));
                        case DENIED_RATE_LIMIT -> Mono.error(new RateLimitExceededException(rateLimitExceptionType));
                        default -> Mono.error(new RateLimitExceededException(rateLimitExceptionType));
                    };
                });
    }
}
