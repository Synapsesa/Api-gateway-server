package com.synapse.api_gateway_server.exception;

import lombok.Getter;

@Getter
public class GlobalRateLimitException extends AbstractGatewayException {
    private final String message;
    private final Throwable cause;

    public GlobalRateLimitException(ExceptionType exceptionType, String message, Throwable cause) {
        super(exceptionType);
        this.message = message;
        this.cause = cause;
    }
}
