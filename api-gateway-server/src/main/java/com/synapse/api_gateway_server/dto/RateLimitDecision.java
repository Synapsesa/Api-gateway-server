package com.synapse.api_gateway_server.dto;

public enum RateLimitDecision {
    ALLOWED(1),
    DENIED_RATE_LIMIT(0), 
    DENIED_MAX_TOTAL(2);

    private final long code;

    RateLimitDecision(long code) {
        this.code = code;
    }

    public long code() {
        return code;
    }

    public static RateLimitDecision fromCode(long decisionCode) {
        for (RateLimitDecision decision : values()) {
            if (decision.code == decisionCode) {
                return decision;
            }
        }
        return DENIED_RATE_LIMIT;
    }
}
