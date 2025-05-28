package com.fx.exchange.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorResponse body = new ErrorResponse(ex.getCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(body);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            HttpMessageConversionException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationErrors(Exception ex) {
        String message;
        if (ex instanceof MissingServletRequestParameterException msrp) {
            message = String.format("Required query parameter '%s' is missing", msrp.getParameterName());
        } else if (ex instanceof MethodArgumentNotValidException manv) {
            message = manv.getBindingResult().getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else if (ex instanceof BindException be) {
            message = be.getBindingResult().getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else {
            message = ex.getMessage();
        }
        ErrorResponse body = new ErrorResponse("VALIDATION_ERROR", message);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMedia(HttpMediaTypeNotSupportedException ex) {
        ErrorResponse body = new ErrorResponse("UNSUPPORTED_MEDIA_TYPE", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(body);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        ErrorResponse body = new ErrorResponse("NOT_FOUND", "No endpoint for " + ex.getRequestURL());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleRestClient(RestClientException ex) {
        ErrorResponse body = new ErrorResponse("EXTERNAL_SERVICE_ERROR", ex.getMessage());
        log.error("Error occurred while calling external api: ", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse body = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
        log.error("Exception: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    /**
     * Handle HTTP method not supported (e.g. POST on a GET-only endpoint).
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        String msg = String.format("'%s' is not supported for this endpoint. Supported methods: %s",
                ex.getMethod(),
                String.join(", ", ex.getSupportedHttpMethods().stream()
                        .map(Object::toString)
                        .toList()));
        ErrorResponse body = new ErrorResponse("METHOD_NOT_ALLOWED", msg);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.ALLOW, String.join(", ",
                        ex.getSupportedHttpMethods().stream()
                                .map(Object::toString)
                                .toList()))
                .body(body);
    }

    public record ErrorResponse(String error, String message) {
    }
}
