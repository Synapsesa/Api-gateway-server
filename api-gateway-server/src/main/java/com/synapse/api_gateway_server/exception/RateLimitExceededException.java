package com.synapse.api_gateway_server.exception;

public class RateLimitExceededException extends AbstractGatewayException {
    public RateLimitExceededException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
