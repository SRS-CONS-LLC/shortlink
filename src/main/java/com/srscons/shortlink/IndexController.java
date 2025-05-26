package com.srscons.shortlink;

import com.srscons.shortlink.shortener.repository.entity.enums.LinkType;
import com.srscons.shortlink.shortener.service.ShortLinkService;
import com.srscons.shortlink.shortener.service.dto.ShortLinkDto;
import com.srscons.shortlink.common.util.UrlUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URI;

@Controller
@RequestMapping
@AllArgsConstructor
public class IndexController {

    private final ShortLinkService shortLinkService;

    @RequestMapping(value = {"/index", "/"})
    public String indexPage() {
        return "landingpage/index";
    }

    @RequestMapping(value = {"/privacy"})
    public String privacyPage() {
        return "landingpage/privacy";
    }

    @RequestMapping(value = {"/terms"})
    public String termsPage() {
        return "landingpage/terms";
    }

    @RequestMapping(value = {"/support"})
    public String supportPage() {
        return "landingpage/support";
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
    public Object previewPage(@PathVariable("shortCode") String shortCode,
                              HttpServletRequest request,
                              Model model) {
        ShortLinkDto shortLink = shortLinkService.getShortLinkByCode(shortCode);
        if (shortLink == null) {
            return "error/404"; // ✅ Show Thymeleaf error page
        }

        shortLinkService.saveVisitMetadata(shortCode, request);

        if (shortLink.getLinkType() == LinkType.BIO) {
            model.addAttribute("link", shortLink);
            return "preview"; // ✅ Show Thymeleaf preview page
        }

        if (shortLink.getLinkType() == LinkType.REDIRECT) {
            String deepLinkUrl = UrlUtils.getDeepLinkUrl(shortLink.getOriginalUrl());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(deepLinkUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND); // ✅ Manual 302 Found redirect
        }

        return "error/404"; // ✅ Fallback
    }


    @GetMapping("/deep/{shortCode}/{linkId}")
    public String redirectToDeepLink(@PathVariable("shortCode") String shortCode,
                                     @PathVariable("linkId") Long linkId,
                                     Model model,
                                     HttpServletRequest request) {
        ShortLinkDto shortLink = shortLinkService.getShortLinkByCode(shortCode);
        if (shortLink == null) {
            return "error/404";
        }

        ShortLinkDto.LinkItemDto linkItem = shortLink.getLinks()
                .stream()
                .filter(link -> link.getId().equals(linkId))
                .findFirst()
                .orElse(null);

        String originalUrl = linkItem.getUrl();

        model.addAttribute("deepLinkUrl", UrlUtils.getDeepLinkUrl(originalUrl));
        model.addAttribute("fallbackUrl", UrlUtils.getFallbackUrl(originalUrl));
        model.addAttribute("appName", UrlUtils.getAppName(originalUrl));

        return "deep-link-redirect";
    }

}
