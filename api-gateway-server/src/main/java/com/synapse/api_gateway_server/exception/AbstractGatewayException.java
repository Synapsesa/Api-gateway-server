package com.synapse.api_gateway_server.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractGatewayException extends RuntimeException {
    
    private final ExceptionType errorCode;
    
    public AbstractGatewayException(String detail, ExceptionType errorCode) {
        super(detail);
        this.errorCode = errorCode;
    }
}
