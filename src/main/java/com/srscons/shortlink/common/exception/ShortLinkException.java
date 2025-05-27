package com.srscons.shortlink.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkException extends RuntimeException {

    private int code;
    private String message;

    public ShortLinkException(String message) {
        super(message);
        this.message = message;
    }

    public ShortLinkException(String message, Throwable cause) {
        super(message, cause);
    }

}
