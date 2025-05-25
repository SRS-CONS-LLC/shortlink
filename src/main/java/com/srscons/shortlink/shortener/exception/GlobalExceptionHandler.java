package com.srscons.shortlink.shortener.exception;


import com.srscons.shortlink.dto.ShortLinkResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ShortLinkResponseDto> handleAll(Throwable ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ShortLinkResponseDto("Unknown error occured. Contact support@srscons.com.", -1, null));
    }

    @ExceptionHandler(ShortLinkException.class)
    public ResponseEntity<ShortLinkResponseDto> handleProjectException(ShortLinkException ex) {
        log.error("ShortLinkException occurred: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ShortLinkResponseDto(ex.getMessage(), -1, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ShortLinkResponseDto> handleValidation(MethodArgumentNotValidException ex) {
        log.error("ShortLinkException occurred: {}", ex.getMessage(), ex);

        StringBuilder message = new StringBuilder("Please check the input data. ");
        ex.getBindingResult().getAllErrors().forEach(error -> message.append(error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ShortLinkResponseDto(message.toString(), -1, null));
    }
}
