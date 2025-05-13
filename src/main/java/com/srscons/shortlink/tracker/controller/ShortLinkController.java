package com.srscons.shortlink.tracker.controller;

import com.srscons.shortlink.tracker.service.ShortLinkService;
import com.srscons.shortlink.tracker.service.MetaDataService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class ShortLinkController {

    private final MetaDataService metaDataService;
    private final ShortLinkService shortLinkService;

    @PostMapping("/shorten")
    public String createShortLink(@RequestParam String url, RedirectAttributes redirectAttributes) {
        String shortCode = shortLinkService.createShortLink(url);
        String shortUrl = "http://localhost:8080/" + shortCode;
        redirectAttributes.addFlashAttribute("shortUrl", shortUrl);
        return "redirect:/dashboard";
    }

    @GetMapping("/{shortCode}")
    public String handleShortLink(@PathVariable String shortCode, Model model) {
        String originalUrl = shortLinkService.getOriginalUrl(shortCode);
        if (originalUrl == null) {
            return "redirect:/not-found";
        }

        model.addAttribute("shortCode", shortCode);
        model.addAttribute("redirectUrl", originalUrl); // JS bu URL-ə yönləndirəcək

        return "intermediate"; // Bu HTML səhifə göstəriləcək (intermediate.html)
    }

    @PostMapping("/visit")
    @ResponseBody
    public void saveMetadata(
            @RequestParam String shortCode,
            @RequestParam String redirectUrl,
            @RequestParam(required = false) String timezone,
            @RequestParam(required = false) String utmSource,
            @RequestParam(required = false) String country,
            HttpServletRequest request
    ) {
        metaDataService.saveMetadata(
                shortCode,
                redirectUrl,
                request,
                timezone,
                utmSource,
                country
        );
    }


}
