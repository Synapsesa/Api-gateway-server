package com.synapse.api_gateway_server.dto;

public record RateLimitResponse(
    long tokensLeft,
    long totalRequests,
    RateLimitPolicy policy
) {
    public static RateLimitResponse from(long tokensLeft, long totalRequests, RateLimitPolicy policy) {
        return new RateLimitResponse(tokensLeft, totalRequests, policy);
    }
}
