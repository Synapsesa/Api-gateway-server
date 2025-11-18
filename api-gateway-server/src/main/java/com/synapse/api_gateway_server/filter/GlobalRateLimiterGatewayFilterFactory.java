package com.synapse.api_gateway_server.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;

import com.synapse.api_gateway_server.dto.RateLimitPolicy;
import com.synapse.api_gateway_server.exception.ExceptionType;
import com.synapse.api_gateway_server.ratelimit.limit.RateLimiter;

import lombok.Getter;
import lombok.Setter;

import static com.synapse.api_gateway_server.utils.RedisKeyConstants.GLOBAL_KEY;

public class GlobalRateLimiterGatewayFilterFactory extends AbstractRateLimiterGatewayFilterFactory<GlobalRateLimiterGatewayFilterFactory.Config> {
    
    public GlobalRateLimiterGatewayFilterFactory(RateLimiter rateLimiter) {
        super(rateLimiter, Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return checkRateLimit(
                GLOBAL_KEY,
                config.getPolicy(),
                exchange,
                chain,
                ExceptionType.GLOBAL_RATE_LIMIT_EXCEEDED,
                ExceptionType.TOTAL_LIMIT_EXCEEDED);
        };
    }

    @Setter
    @Getter
    public static class Config {
        private RateLimitPolicy policy;
    }
}
