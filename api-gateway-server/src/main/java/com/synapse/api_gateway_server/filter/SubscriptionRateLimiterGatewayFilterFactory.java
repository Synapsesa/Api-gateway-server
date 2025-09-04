package com.synapse.api_gateway_server.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import com.synapse.api_gateway_server.ratelimit.limit.RateLimiter;

public class SubscriptionRateLimiterGatewayFilterFactory extends AbstractRateLimiterGatewayFilterFactory {
    
    public SubscriptionRateLimiterGatewayFilterFactory(RateLimiter rateLimiter) {
        super(rateLimiter);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'apply'");
    }
}
