package com.techcorp.employee.dto;

import java.time.Instant;

public class ErrorResponse {
    private String message;
    private Instant timestamp;
    private int status;
    private String path;

    public ErrorResponse(String message, int status, String path) {
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = Instant.now();
    }

    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getPath() { return path; }
}