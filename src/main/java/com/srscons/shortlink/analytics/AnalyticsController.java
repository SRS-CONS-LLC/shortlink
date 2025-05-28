package com.srscons.shortlink.analytics;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/short-links")
@RequiredArgsConstructor
public class AnalyticsController {

    private final ShortLinkAnalyticsService analyticsService;

    @GetMapping("/{shortLinkId}/analytics")
    public ResponseEntity<AnalyticsDTO> getAnalytics(@PathVariable Long shortLinkId) {
        return ResponseEntity.ok(analyticsService.getAnalytics(shortLinkId));
    }
}
