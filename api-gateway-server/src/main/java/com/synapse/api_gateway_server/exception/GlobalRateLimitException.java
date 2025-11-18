package com.synapse.api_gateway_server.exception;

public class GlobalRateLimitException extends AbstractGatewayException {
    public GlobalRateLimitException(ExceptionType exceptionType, String message, Throwable cause) {
        super(message, cause, exceptionType);
    }
}
