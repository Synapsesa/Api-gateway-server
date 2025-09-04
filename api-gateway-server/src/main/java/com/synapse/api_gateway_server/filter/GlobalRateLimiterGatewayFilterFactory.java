package com.synapse.api_gateway_server.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import com.synapse.api_gateway_server.ratelimit.limit.RateLimiter;

import static com.synapse.api_gateway_server.utils.RedisKeyConstants.GLOBAL_KEY;

public class GlobalRateLimiterGatewayFilterFactory extends AbstractRateLimiterGatewayFilterFactory {
    
    public GlobalRateLimiterGatewayFilterFactory(RateLimiter rateLimiter) {
        super(rateLimiter);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return checkRateLimit(GLOBAL_KEY, config.getPolicy(), exchange, chain);
        };
    }
}
