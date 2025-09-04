package com.synapse.api_gateway_server.exception;

public class InvalidTokenException extends AbstractGatewayException {
    public InvalidTokenException(String detail) {
        super(detail, ExceptionType.UNAUTHENTICATED);
    }
}
