package com.srscons.shortlink.shortener.exception;

public class QrCodeGenerationException extends RuntimeException{
    public QrCodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
