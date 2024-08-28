package com.fsoft.fsa.kindergarten.config.exception;

public class UserEmailExistException extends RuntimeException{
    public UserEmailExistException(String message) {
        super(message);
    }

    public UserEmailExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
