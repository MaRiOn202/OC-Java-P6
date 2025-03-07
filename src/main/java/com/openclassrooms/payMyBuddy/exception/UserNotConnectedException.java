package com.openclassrooms.payMyBuddy.exception;

public class UserNotConnectedException extends RuntimeException {

    public UserNotConnectedException(String message) {

        super(message);

    }

    public UserNotConnectedException(String message, Throwable cause) {

        super(message, cause);

    }




}
