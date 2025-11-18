package com.synapse.api_gateway_server.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RateLimitDecisionTest {

    @Test
    @DisplayName("DECISION_CODE 1 → ALLOWED")
    void fromCodeAllowed() {
        assertEquals(RateLimitDecision.ALLOWED, RateLimitDecision.fromCode(1));
    }

    @Test
    @DisplayName("DECISION_CODE 0 → DENIED_RATE_LIMIT")
    void fromCodeRateLimited() {
        assertEquals(RateLimitDecision.DENIED_RATE_LIMIT, RateLimitDecision.fromCode(0));
    }

    @Test
    @DisplayName("DECISION_CODE 2 → DENIED_MAX_TOTAL")
    void fromCodeMaxTotal() {
        assertEquals(RateLimitDecision.DENIED_MAX_TOTAL, RateLimitDecision.fromCode(2));
    }

    @Test
    @DisplayName("알 수 없는 코드 → 기본 DENIED_RATE_LIMIT")
    void fromCodeUnknownDefaultsToRateLimited() {
        assertEquals(RateLimitDecision.DENIED_RATE_LIMIT, RateLimitDecision.fromCode(99));
    }
}
