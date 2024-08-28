package com.fsoft.fsa.kindergarten.config.exception;

public class FeeInvalidException extends RuntimeException{
    public FeeInvalidException(String message) {
        super(message);
    }

    public FeeInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
