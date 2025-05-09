package com.openclassrooms.payMyBuddy.exception;

public class EmailAlreadyExistingException extends RuntimeException {

    public EmailAlreadyExistingException(String message) {
         super(message);
    }

    public EmailAlreadyExistingException(String message, Throwable cause) {
        super(message, cause);

    }

}
