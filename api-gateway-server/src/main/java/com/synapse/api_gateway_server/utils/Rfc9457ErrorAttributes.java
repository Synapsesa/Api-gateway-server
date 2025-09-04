package com.synapse.api_gateway_server.utils;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.synapse.api_gateway_server.exception.AbstractGatewayException;
import com.synapse.api_gateway_server.exception.ExceptionType;

@Component
public class Rfc9457ErrorAttributes extends DefaultErrorAttributes {
    
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

        Throwable error = getError(request);

        if (error instanceof AbstractGatewayException ex) {
            ExceptionType exceptionType = ex.getErrorCode();

            errorAttributes.put("type", "/docs/errors/" + exceptionType.getCode());
            errorAttributes.put("title", exceptionType.getTitle());
            errorAttributes.put("status", exceptionType.getStatus().value());
            errorAttributes.put("detail", ex.getMessage());
            errorAttributes.put("instance", errorAttributes.get("path"));
            errorAttributes.put("code", exceptionType.getCode());
        } else {
            int status = (int) errorAttributes.getOrDefault("status", 500);
            errorAttributes.put("type", "/docs/errors/G999");
            errorAttributes.put("title", "Internal Server Error");
            errorAttributes.put("status", status);
            errorAttributes.put("detail", errorAttributes.get("message"));
            errorAttributes.put("instance", errorAttributes.get("path"));
            errorAttributes.put("code", "G999");
        }

        errorAttributes.remove("error");
        errorAttributes.remove("requestId");
        errorAttributes.remove("timestamp");
        errorAttributes.remove("path");
        errorAttributes.remove("message");

        return errorAttributes;
    }
}
