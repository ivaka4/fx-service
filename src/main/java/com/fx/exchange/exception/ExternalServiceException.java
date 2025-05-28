package com.fx.exchange.exception;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends ApiException {
    public ExternalServiceException(String detail, Throwable cause) {
        super(HttpStatus.BAD_GATEWAY,
                "EXTERNAL_SERVICE_ERROR",
                "Failed to fetch FX rates: " + detail);
        initCause(cause);
    }

    public ExternalServiceException(String detail) {
        this(detail, null);
    }
}
