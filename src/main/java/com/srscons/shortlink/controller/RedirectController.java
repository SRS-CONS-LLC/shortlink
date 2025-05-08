package com.srscons.shortlink.controller;

import com.srscons.shortlink.model.ShortLink;
import com.srscons.shortlink.repository.ShortLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.util.HtmlUtils;

@RestController
public class RedirectController {

    @Autowired
    private ShortLinkRepository shortLinkRepository;

    @GetMapping("/{shortCode}")
    public ResponseEntity<String> redirectToPlatform(
            @PathVariable String shortCode,
            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent) {

        ShortLink link = shortLinkRepository.findByCode(shortCode);
        if (link == null) {
            return ResponseEntity.notFound().build();
        }

        String lowerAgent = userAgent.toLowerCase();
        String deepLink = HtmlUtils.htmlEscape(determineRedirectUrl(lowerAgent, link));
        String fallbackUrl = HtmlUtils.htmlEscape(
                link.getOriginalUrl() != null ? link.getOriginalUrl() : "https://relink.is"
        );

        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <meta charset='UTF-8'>\n" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "  <title>Redirecting...</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<script>\n" +
                "  var deepLink = '" + deepLink + "';\n" +
                "  var fallbackUrl = '" + fallbackUrl + "';\n" +
                "  window.location = deepLink;\n" +
                "  setTimeout(function() {\n" +
                "    window.location = fallbackUrl;\n" +
                "  }, 2000);\n" +
                "</script>\n" +
                "<p>Yönləndirilirsiniz...</p>\n" +
                "</body>\n" +
                "</html>";

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
    }

    private String determineRedirectUrl(String userAgent, ShortLink link) {
        if (userAgent.contains("instagram")) {
            return link.getInstagramDeepLink() != null ? link.getInstagramDeepLink() : link.getInstagramUrl();
        } else if (userAgent.contains("tiktok")) {
            return link.getTiktokDeepLink() != null ? link.getTiktokDeepLink() : link.getTiktokUrl();
        } else if (userAgent.contains("youtube")) {
            return link.getYoutubeDeepLink() != null ? link.getYoutubeDeepLink() : link.getYoutubeUrl();
        }
        return link.getOriginalUrl() != null ? link.getOriginalUrl() : "https://relink.is";
    }
}
