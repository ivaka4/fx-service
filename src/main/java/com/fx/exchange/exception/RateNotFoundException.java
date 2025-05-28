package com.fx.exchange.exception;

import org.springframework.http.HttpStatus;

public class RateNotFoundException extends ApiException {
    public RateNotFoundException(String message) {
        super(HttpStatus.BAD_REQUEST, "RATE_NOT_FOUND", message);
    }
}
