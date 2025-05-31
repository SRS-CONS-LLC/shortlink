package com.srscons.shortlink.analytics;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/short-links")
@RequiredArgsConstructor
public class AnalyticsController {
    private final ShortLinkAnalyticsService analyticsService;

    @GetMapping("/{shortLinkId}/analytics")
    public ResponseEntity<AnalyticsDTO> getAnalytics(
            @PathVariable Long shortLinkId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getAnalytics(shortLinkId, startDate, endDate));
    }
}
