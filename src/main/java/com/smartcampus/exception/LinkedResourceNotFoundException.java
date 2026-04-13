package com.smartcampus.exception;

// Exception when a referenced resource (e.g. roomId) does not exist
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}
