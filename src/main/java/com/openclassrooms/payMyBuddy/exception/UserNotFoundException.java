package com.openclassrooms.payMyBuddy.exception;

import java.io.IOException;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {

        super(message);

    }

    public UserNotFoundException(String message, Throwable cause) {

        super(message, cause);

    }
}
