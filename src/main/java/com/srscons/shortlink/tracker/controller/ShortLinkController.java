package com.srscons.shortlink.tracker.controller;

import com.srscons.shortlink.tracker.dto.CreateShortLinkRequest;
import com.srscons.shortlink.tracker.dto.ShortLinkResponse;
import com.srscons.shortlink.tracker.service.ShortLinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortLinkResponse> createShortLink(@Valid @RequestBody CreateShortLinkRequest request) {
        ShortLinkResponse response = shortLinkService.createShortLink(request.getUrl());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> handleShortLink(@PathVariable String shortCode, HttpServletRequest request) {
        String originalUrl = shortLinkService.getOriginalUrl(shortCode);
        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }

        // Save metadata about this visit
        shortLinkService.saveVisitMetadata(shortCode, originalUrl, request);

        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }
}
