package com.synapse.api_gateway_server.exception;

import lombok.Getter;

@Getter
public abstract class AbstractGatewayException extends RuntimeException {

    private final ExceptionType errorCode;

    protected AbstractGatewayException(ExceptionType errorCode) {
        super(errorCode.getTitle());
        this.errorCode = errorCode;
    }

    protected AbstractGatewayException(String detail, ExceptionType errorCode) {
        super(detail);
        this.errorCode = errorCode;
    }

    protected AbstractGatewayException(String detail, Throwable cause, ExceptionType errorCode) {
        super(detail, cause);
        this.errorCode = errorCode;
    }
}
