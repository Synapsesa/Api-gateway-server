package com.synapse.api_gateway_server.dto;

public enum RateLimitDecision {
    ALLOWED,
    DENIED_RATE_LIMIT, 
    DENIED_MAX_TOTAL
}
