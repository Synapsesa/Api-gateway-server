package com.synapse.api_gateway_server.exception;

public class GlobalRateLimitException extends AbstractGatewayException {
    public GlobalRateLimitException() {
        super(ExceptionType.GLOBAL_RATE_LIMIT_EXCEEDED);
    }
}
