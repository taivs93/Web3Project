package com.kunfeng2002.be002.exception;

public class FollowException extends RuntimeException {
    public FollowException(String message) {
        super(message);
    }

    public FollowException(String message, Throwable cause) {
        super(message, cause);
    }
}
