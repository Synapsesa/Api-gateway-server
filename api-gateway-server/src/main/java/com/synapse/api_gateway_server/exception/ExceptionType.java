package com.synapse.api_gateway_server.exception;

import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {

    // 400 Bad Request
    INVALID_INPUT_VALUE("C001", "Invalid Input Value", BAD_REQUEST),
    INVALID_AUTHORIZATION_HEADER("A001", "Invalid Authorization Header", BAD_REQUEST),

    // 401 Unauthorized
    UNAUTHENTICATED("A002", "Unauthenticated", UNAUTHORIZED),
    JWT_TOKEN_EXPIRED("A003", "JWT Token Expired", UNAUTHORIZED),

    // 403 Forbidden
    ACCESS_DENIED("A004", "Access Denied", FORBIDDEN),

    // 404 Not Found
    ROUTE_NOT_FOUND("R001", "Route Not Found", NOT_FOUND),
    POLICY_NOT_FOUND("P001", "Policy Not Found", NOT_FOUND),

    // 429 Too Many Requests
    IP_RATE_LIMIT_EXCEEDED("R002", "IP Rate Limit Exceeded", TOO_MANY_REQUESTS),
    GLOBAL_RATE_LIMIT_EXCEEDED("R003", "Global Rate Limit Exceeded", TOO_MANY_REQUESTS),
    
    // 500 Internal Server Error
    INTERNAL_SERVER_ERRORS("S001", "Internal Server Error", INTERNAL_SERVER_ERROR),
    RATE_LIMIT_CHECK_FAILED("S002", "Failed to Check Rate Limit", INTERNAL_SERVER_ERROR),

    // 503 Service Unavailable
    SERVICE_UNAVAILABLES("S001", "Service Temporarily Unavailable", SERVICE_UNAVAILABLE);
    
    private final String code;
    private final String title;
    private final HttpStatus status;
}
