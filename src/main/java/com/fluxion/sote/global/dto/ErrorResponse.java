package com.fluxion.sote.global.dto;

import java.time.ZonedDateTime;
import java.util.Map;

public class ErrorResponse {
    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validation;  // validation errors

    public ErrorResponse(ZonedDateTime timestamp,
                         int status,
                         String error,
                         String message,
                         String path) {
        this(timestamp, status, error, message, path, null);
    }

    public ErrorResponse(ZonedDateTime timestamp,
                         int status,
                         String error,
                         String message,
                         String path,
                         Map<String, String> validation) {
        this.timestamp = timestamp;
        this.status    = status;
        this.error     = error;
        this.message   = message;
        this.path      = path;
        this.validation = validation;
    }

    // getters only...
}
