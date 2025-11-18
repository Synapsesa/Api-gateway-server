package com.synapse.api_gateway_server.exception;

public class InvalidConfigurationException extends AbstractGatewayException {
    public InvalidConfigurationException(ExceptionType exceptionType) {
        super(exceptionType);
    }    
}
