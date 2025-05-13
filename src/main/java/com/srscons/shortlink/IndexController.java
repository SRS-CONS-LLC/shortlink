package com.srscons.shortlink;

import com.srscons.shortlink.linkinbio.service.ShortLinkService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@AllArgsConstructor
public class IndexController {

    private final ShortLinkService shortLinkService;

    @RequestMapping(value = {"/index", "/"})
    public String indexPage() {
        return "index";
    }

    @RequestMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }

    @RequestMapping("/plan")
    public String planPage() {
        return "plan";
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> handleShortLink(
            @PathVariable("shortCode") String shortCode,
            HttpServletRequest request) {
        String originalUrl = shortLinkService.getOriginalUrl(shortCode);
        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }

        shortLinkService.saveVisitMetadata(shortCode, request);
        return ResponseEntity.status(302).header("Location", originalUrl).build();
    }
}
