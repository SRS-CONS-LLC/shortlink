package com.srscons.shortlink;

import com.srscons.shortlink.shortener.repository.entity.enums.LinkType;
import com.srscons.shortlink.shortener.service.ShortLinkService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        var shortLink = shortLinkService.getShortLinkByCode(shortCode);
        if (shortLink == null) {
            return ResponseEntity.notFound().build();
        }

        shortLinkService.saveVisitMetadata(shortCode, request);

        if (shortLink.getLinkType() == LinkType.REDIRECT) {
            return ResponseEntity.status(302).header("Location", shortLink.getOriginalUrl()).build();
        } else {
            // For bio type, redirect to the preview page
            return ResponseEntity.status(302).header("Location", "/preview/" + shortCode).build();
        }
    }

    @GetMapping("/preview/{shortCode}")
    public String previewPage(@PathVariable("shortCode") String shortCode, Model model) {
        var shortLink = shortLinkService.getShortLinkByCode(shortCode);
        if (shortLink == null) {
            return "error/404";
        }

        if (shortLink.getLinkType() != LinkType.BIO) {
            return "error/404";
        }

        model.addAttribute("shortLink", shortLink);
        return "preview";
    }
}
