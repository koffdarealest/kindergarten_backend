package com.fsoft.fsa.kindergarten.config.exception;

public class StatusChangeException extends RuntimeException {
    public StatusChangeException(String message) {
        super(message);
    }

    public StatusChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
