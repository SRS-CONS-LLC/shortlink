package com.srscons.shortlink.linkinbio.exception;

public class ShortLinkNotFoundException extends RuntimeException {
    public ShortLinkNotFoundException(Long id) {
        super("Short link not found with id: " + id);
    }

    public ShortLinkNotFoundException(String shortCode) {
        super("Short link not found with code: " + shortCode);
    }
} 