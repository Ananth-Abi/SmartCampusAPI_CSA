package com.smartcampus.exception;

// Exception when a requested resource is not found
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}
