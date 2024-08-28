package com.fsoft.fsa.kindergarten.config.exception;

public class NoAuthorityException extends RuntimeException{
    public NoAuthorityException(String message) {
        super(message);
    }

    public NoAuthorityException(String message, Throwable cause) {
        super(message, cause);
    }
}
