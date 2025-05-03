package com.srscons.shortlink.controller;

import com.srscons.shortlink.service.ShortLinkService;
import com.srscons.shortlink.service.impl.ShortLinkServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ShortLinkController {

    private final ShortLinkServiceImpl service;

    @PostMapping("/shorten")
    public ResponseEntity<String> shorten(@RequestBody String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            return ResponseEntity.badRequest().body("Original URL is required.");
        }

        String shortLink = service.createShortLink(originalUrl);
        return ResponseEntity.ok(shortLink);
    }

    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        String originalUrl = service.getOriginalUrl(shortCode);
        if (originalUrl != null) {
            response.sendRedirect(originalUrl);  // real redirect
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<String> stats(@PathVariable String shortCode) {
        long count = service.getClickStats(shortCode);
        return ResponseEntity.ok(String.valueOf(count));

    }
}
