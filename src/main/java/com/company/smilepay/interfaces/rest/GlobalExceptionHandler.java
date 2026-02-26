package com.company.smilepay.interfaces.rest;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<?> handleGatewayError(WebClientResponseException ex) {

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(
                        java.util.Map.of(
                                "status", "FAILED",
                                "gatewayStatus", ex.getStatusCode().value(),
                                "message", ex.getResponseBodyAsString()
                        )
                );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        java.util.Map.of(
                                "status", "FAILED",
                                "message", ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        java.util.Map.of(
                                "status", "ERROR",
                                "message", "Unexpected server error"
                        )
                );
    }
}