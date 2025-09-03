package com.synapse.api_gateway_server.configuration;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;

import com.synapse.api_gateway_server.exception.GlobalErrorWebExceptionHandler;

@Configuration
public class ApiGatewayServerConfig {
    @Bean
    public WebProperties.Resources webPropertiesResources() {
        return new WebProperties.Resources();
    }

    @Bean
    public ErrorWebExceptionHandler errorWebExceptionHandler(
        ErrorAttributes errorAttributes,
        WebProperties.Resources resources,
        ApplicationContext applicationContext,
        ServerCodecConfigurer configurer
    ) {

        GlobalErrorWebExceptionHandler exceptionHandler = new GlobalErrorWebExceptionHandler(errorAttributes, resources, applicationContext);
        exceptionHandler.setMessageWriters(configurer.getWriters());
        exceptionHandler.setMessageReaders(configurer.getReaders());
        return exceptionHandler;
    }
}
