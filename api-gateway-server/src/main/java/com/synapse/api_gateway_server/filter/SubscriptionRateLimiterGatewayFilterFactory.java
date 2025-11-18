package com.synapse.api_gateway_server.filter;

import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.web.server.ServerWebExchange;

import com.synapse.api_gateway_server.dto.RateLimitPolicy;
// import com.synapse.api_gateway_server.exception.ExceptionType;
// import com.synapse.api_gateway_server.exception.InvalidConfigurationException;
// import com.synapse.api_gateway_server.exception.RateLimitExceededException;
import com.synapse.api_gateway_server.ratelimit.limit.RateLimiter;
import com.synapse.synapse_domain_model.SubscriptionTier;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

public class SubscriptionRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<SubscriptionRateLimiterGatewayFilterFactory.Config> {
    
    // private final RateLimiter rateLimiter;
    // private final QueueEventPublisher queueEventPublisher;

    public SubscriptionRateLimiterGatewayFilterFactory(RateLimiter rateLimiter) {
        super(Config.class); // redis publisher 의존성 주입 필요
        // this.rateLimiter = rateLimiter;
        // this.queueEventPublisher = queueEventPublisher;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return null;
        // return (exchange, chain) -> {
            // AuthenticatedUser user = exchange.getAttribute(AUTHENTICATED_USER_ATTR); // 이는 라이브러리에서 제공

            // if (user == null) {
            //     return chain.filter(exchange);
            // }

            // RateLimitPolicy policy = config.getPolicies().get(user.getTier());
            // if (policy == null) {
            //     return Mono.error(new InvalidConfigurationException(ExceptionType.POLICY_NOT_FOUND));
            // }

            // return rateLimiter.check(user.getUserId(), policy)
            //         .flatMap(response -> {
            //             exchange.getResponse().getHeaders().add("X-RateLimit-Remaining",
            //                     String.valueOf(response.tokensLeft()));

            //             switch (response.decision()) {
            //                 case ALLOWED:
            //                     return chain.filter(exchange);
            //                 case DENIED_RATE_LIMIT:
            //                     if (user.getTier() == SubscriptionTier.PRO) {
            //                         return handleQueueing(exchange, user);
            //                     } else {
            //                         log.warn("Rate limit exceeded for FREE user '{}'.", user.getUserId());
            //                         return Mono.error(new RateLimitExceededException(ExceptionType.RATE_LIMIT_EXCEEDED));
            //                     }

            //                 case DENIED_MAX_TOTAL:
            //                 default:
            //                     return Mono.error(new RateLimitExceededException(ExceptionType.TOTAL_LIMIT_EXCEEDED));
            //             }
            //         });
        // };
    }

    private Mono<Void> handleQueueing(ServerWebExchange exchange) {
        return Mono.empty();
        // return queueEventPublisher.publish(exchange.getRequest(), user) // 라이브러리에서 제공하도록 작성되어야 함
        //         .then(Mono.defer(() -> {
        //             exchange.getResponse().setStatusCode(HttpStatus.ACCEPTED); // 202 Accepted
        //             return exchange.getResponse().setComplete();
        //         }));
    }

    @Setter
    @Getter
    public static class Config {
        private Map<SubscriptionTier, RateLimitPolicy> policies;
    }
}
