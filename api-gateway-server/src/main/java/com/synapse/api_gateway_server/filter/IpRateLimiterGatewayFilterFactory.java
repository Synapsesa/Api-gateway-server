package com.synapse.api_gateway_server.filter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;

import com.synapse.api_gateway_server.dto.RateLimitPolicy;
import com.synapse.api_gateway_server.exception.ExceptionType;
import com.synapse.api_gateway_server.ratelimit.limit.RateLimiter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IpRateLimiterGatewayFilterFactory extends AbstractRateLimiterGatewayFilterFactory<IpRateLimiterGatewayFilterFactory.Config> {
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String X_REAL_IP = "X-Real-IP";
    public static final String X_ORIGINATING_IP = "X-Originating-IP";
    private static final String UNKNOWN_IP = "unknown";
    
    public IpRateLimiterGatewayFilterFactory(RateLimiter rateLimiter) {
        super(rateLimiter, Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientIp = extractClientIp(exchange.getRequest());
            return checkRateLimit(
                clientIp,
                config.getPolicy(),
                exchange,
                chain,
                ExceptionType.IP_RATE_LIMIT_EXCEEDED,
                ExceptionType.TOTAL_LIMIT_EXCEEDED);
        };
    }

    private String extractClientIp(ServerHttpRequest request) {
        List<String> headerNames = List.of(X_FORWARDED_FOR, X_REAL_IP, X_ORIGINATING_IP);

        Optional<String> ipFromHeaders = headerNames.stream()
                .map(headerName -> {
                    String headerValue = request.getHeaders().getFirst(headerName);
                    return extractAndValidateIp(headerValue, headerName.equals(X_FORWARDED_FOR));
                })
                .filter(Objects::nonNull)
                .findFirst();

        if (ipFromHeaders.isPresent()) {
            return ipFromHeaders.get();
        }

        String remoteAddress = Optional.ofNullable(request.getRemoteAddress())
                .map(address -> address.getAddress().getHostAddress())
                .orElse(null);

        String ipFromRemoteAddr = extractAndValidateIp(remoteAddress, false);
        if (ipFromRemoteAddr != null) {
            return ipFromRemoteAddr;
        }

        log.warn("Unable to extract client IP from request. Using fallback value: {}", UNKNOWN_IP);
        return UNKNOWN_IP;
    }

    private String extractAndValidateIp(String ipValue, boolean isForwardedFor) {
        if (ipValue == null || ipValue.trim().isEmpty()) {
            return null;
        }

        String candidateIp;
        if (isForwardedFor) {
            int commaIndex = ipValue.indexOf(',');
            candidateIp = commaIndex > 0 ? ipValue.substring(0, commaIndex).trim() : ipValue.trim();
        } else {
            candidateIp = ipValue.trim();
        }

        return isValidIp(candidateIp) ? candidateIp : null;
    }

    private boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        return !ip.equalsIgnoreCase("unknown") &&
                !ip.equals("0:0:0:0:0:0:0:1") &&
                !ip.startsWith("127.") &&
                !ip.equals("::1");
    }

    @Setter
    @Getter
    public static class Config {
        private RateLimitPolicy policy;
    }
}
