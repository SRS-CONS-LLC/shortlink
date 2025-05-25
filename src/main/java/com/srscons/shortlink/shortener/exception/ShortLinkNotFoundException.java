package com.srscons.shortlink.shortener.exception;

public class ShortLinkNotFoundException extends ShortLinkException {

    public ShortLinkNotFoundException(Long id) {
        super("Short link not found with id: " + id);
    }

    public ShortLinkNotFoundException(String shortCode) {
        super("Short link not found with code: " + shortCode);
    }
} 