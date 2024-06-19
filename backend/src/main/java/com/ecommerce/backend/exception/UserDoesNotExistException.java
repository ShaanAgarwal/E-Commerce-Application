package com.ecommerce.backend.exception;

public class UserDoesNotExistException extends RuntimeException{
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
