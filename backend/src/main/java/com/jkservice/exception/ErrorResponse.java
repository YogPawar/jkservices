package com.jkservice.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant localDateTime, int status, String error, String message, String path) {}
