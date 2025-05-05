package com.srscons.shortlink.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ShortLinkService;

import java.net.URI;

@RestController
public class RedirectController {

    @Autowired
    private ShortLinkService shortLinkService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = shortLinkService.getOriginalUrlByCode(shortCode);

        if (originalUrl != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(originalUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }
}
