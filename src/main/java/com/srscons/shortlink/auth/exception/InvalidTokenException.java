package com.srscons.shortlink.auth.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidTokenException of(String token) {
        return new InvalidTokenException("Invalid token: " + token);
    }

}
