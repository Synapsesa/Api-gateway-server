package com.synapse.api_gateway_server.dto;

public record RateLimitPolicy(
    long replenishRate,
    long burstCapacity,
    long requestedTokens,
    long maxTotalRequests,
    long totalRequestsTtlSeconds
) {
    public RateLimitPolicy(long replenishRate, long burstCapacity, long maxTotalRequests, long totalRequestsTtlSeconds) {
        this(replenishRate, burstCapacity, 1, maxTotalRequests, totalRequestsTtlSeconds);
    }
}
