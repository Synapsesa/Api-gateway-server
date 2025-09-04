package com.synapse.api_gateway_server.ratelimit.limit;

import com.synapse.api_gateway_server.dto.RateLimitPolicy;
import com.synapse.api_gateway_server.dto.RateLimitResponse;

import reactor.core.publisher.Mono;

public interface RateLimiter {
    Mono<RateLimitResponse> check(String key, RateLimitPolicy policy);
}
