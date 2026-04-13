package com.smartcampus.exception;

// Exception when a room still has sensors and cannot be deleted
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}
