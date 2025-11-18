package com.synapse.api_gateway_server.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import com.synapse.api_gateway_server.dto.RateLimitDecision;
import com.synapse.api_gateway_server.dto.RateLimitPolicy;
import com.synapse.api_gateway_server.dto.RateLimitResponse;
import com.synapse.api_gateway_server.exception.ExceptionType;
import com.synapse.api_gateway_server.exception.RateLimitExceededException;
import com.synapse.api_gateway_server.ratelimit.limit.RateLimiter;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AbstractRateLimiterGatewayFilterFactoryTest {

    private static final RateLimitPolicy POLICY = new RateLimitPolicy(1, 5, 10, 60);

    @Test
    void allowedDecisionContinuesChain() {
        StubRateLimiter stubRateLimiter = new StubRateLimiter();
        TestGatewayFilterFactory factory = new TestGatewayFilterFactory(stubRateLimiter);
        StubGatewayFilterChain chain = new StubGatewayFilterChain();

        RateLimitResponse response = RateLimitResponse.from(3, 1, POLICY, RateLimitDecision.ALLOWED);
        Mono<Void> result = factory.invoke(response, POLICY, chain,
                ExceptionType.RATE_LIMIT_EXCEEDED,
                ExceptionType.TOTAL_LIMIT_EXCEEDED);

        StepVerifier.create(result).verifyComplete();
        assertTrue(chain.invoked());
    }

    @Test
    void deniedRateLimitThrowsConfiguredException() {
        StubRateLimiter stubRateLimiter = new StubRateLimiter();
        TestGatewayFilterFactory factory = new TestGatewayFilterFactory(stubRateLimiter);
        StubGatewayFilterChain chain = new StubGatewayFilterChain();

        RateLimitResponse response = RateLimitResponse.from(0, 5, POLICY, RateLimitDecision.DENIED_RATE_LIMIT);
        Mono<Void> result = factory.invoke(response, POLICY, chain,
                ExceptionType.IP_RATE_LIMIT_EXCEEDED,
                ExceptionType.TOTAL_LIMIT_EXCEEDED);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof RateLimitExceededException);
                    RateLimitExceededException ex = (RateLimitExceededException) error;
                    assertEquals(ExceptionType.IP_RATE_LIMIT_EXCEEDED, ex.getErrorCode());
                })
                .verify();

        assertFalse(chain.invoked());
    }

    @Test
    void deniedMaxTotalThrowsConfiguredException() {
        StubRateLimiter stubRateLimiter = new StubRateLimiter();
        TestGatewayFilterFactory factory = new TestGatewayFilterFactory(stubRateLimiter);
        StubGatewayFilterChain chain = new StubGatewayFilterChain();

        RateLimitResponse response = RateLimitResponse.from(0, 11, POLICY, RateLimitDecision.DENIED_MAX_TOTAL);
        Mono<Void> result = factory.invoke(response, POLICY, chain,
                ExceptionType.RATE_LIMIT_EXCEEDED,
                ExceptionType.TOTAL_LIMIT_EXCEEDED);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof RateLimitExceededException);
                    RateLimitExceededException ex = (RateLimitExceededException) error;
                    assertEquals(ExceptionType.TOTAL_LIMIT_EXCEEDED, ex.getErrorCode());
                })
                .verify();

        assertFalse(chain.invoked());
    }

    private static class StubRateLimiter implements RateLimiter {
        private final AtomicReference<RateLimitResponse> nextResponse = new AtomicReference<>();

        void setResponse(RateLimitResponse response) {
            nextResponse.set(response);
        }

        @Override
        public Mono<RateLimitResponse> check(String id, RateLimitPolicy policy) {
            RateLimitResponse response = nextResponse.get();
            if (response == null) {
                return Mono.error(new IllegalStateException("Response not configured"));
            }
            return Mono.just(response);
        }
    }

    private static class TestGatewayFilterFactory extends AbstractRateLimiterGatewayFilterFactory<TestGatewayFilterFactory.Config> {

        private final StubRateLimiter rateLimiter;

        protected TestGatewayFilterFactory(StubRateLimiter rateLimiter) {
            super(rateLimiter, Config.class);
            this.rateLimiter = rateLimiter;
        } 

        @Override
        public GatewayFilter apply(Config config) {
            throw new UnsupportedOperationException("Test stub does not use apply");
        }

        Mono<Void> invoke(RateLimitResponse response,
                          RateLimitPolicy policy,
                          GatewayFilterChain chain,
                          ExceptionType rateLimitExceptionType,
                          ExceptionType totalLimitExceptionType) {
            rateLimiter.setResponse(response);
            MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
            ServerWebExchange exchange = MockServerWebExchange.from(request);
            return checkRateLimit("test-key", policy, exchange, chain, rateLimitExceptionType, totalLimitExceptionType);
        }

        static class Config { }
    }

    private static class StubGatewayFilterChain implements GatewayFilterChain {
        private final AtomicBoolean invoked = new AtomicBoolean(false);

        @Override
        public Mono<Void> filter(ServerWebExchange exchange) {
            invoked.set(true);
            return Mono.empty();
        }

        boolean invoked() {
            return invoked.get();
        }
    }
}
