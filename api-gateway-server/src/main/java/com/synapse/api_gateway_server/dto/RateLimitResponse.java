package com.synapse.api_gateway_server.dto;

public record RateLimitResponse(
    long tokensLeft,
    long totalRequests,
    RateLimitPolicy policy,
    RateLimitDecision decision
) {
    public static RateLimitResponse from(long tokensLeft, long totalRequests, RateLimitPolicy policy, RateLimitDecision decision) {
        return new RateLimitResponse(tokensLeft, totalRequests, policy, decision);
    }

    public boolean isAllowed() {
        return decision == RateLimitDecision.ALLOWED;
    }
}
