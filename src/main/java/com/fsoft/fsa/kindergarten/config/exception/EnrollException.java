package com.fsoft.fsa.kindergarten.config.exception;

public class EnrollException extends RuntimeException {
    public EnrollException(String message) {
        super(message);
    }

    public EnrollException(String message, Throwable cause) {
        super(message, cause);
    }
}
