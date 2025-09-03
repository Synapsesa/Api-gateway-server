package com.synapse.api_gateway_server.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final ExceptionType errorCode;
    
    public BusinessException(ExceptionType errorCode) {
        super(errorCode.getTitle());
        this.errorCode = errorCode;
    }
    
    public BusinessException(String detail, ExceptionType errorCode) {
        super(detail);
        this.errorCode = errorCode;
    }
}
