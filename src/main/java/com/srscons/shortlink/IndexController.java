package com.srscons.shortlink;

import com.srscons.shortlink.shortener.repository.entity.enums.LinkType;
import com.srscons.shortlink.shortener.service.ShortLinkService;
import com.srscons.shortlink.shortener.service.dto.ShortLinkDto;
import com.srscons.shortlink.common.util.UrlUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
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
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

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
            return redirect(shortLink.getOriginalUrl(), request.getHeader("User-Agent"));
        }

        return "error/404"; // ✅ Fallback
    }


    @GetMapping("/deep/{shortCode}/{linkId}")
    public Object redirectToDeepLink(@PathVariable("shortCode") String shortCode,
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

        return redirect(linkItem.getUrl(), request.getHeader("User-Agent"));
    }

    private static Object redirect(String deepLinkUrl, String userAgent) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Vary", Collections.singletonList("User-Agent"));
        headers.setLocation(URI.create(getRedirectUrl(deepLinkUrl, userAgent)));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }


    public static String getRedirectUrl(String originalUrl, String userAgent) {
        if (originalUrl == null || userAgent == null) {
            return originalUrl;
        }

        String fallbackUrl = originalUrl;
        String videoId = null;
        String channelHandle = null;

        try {
            URI uri = new URI(originalUrl);
            String host = uri.getHost();
            String path = uri.getPath();

            // Check if it's a video URL
            if (originalUrl.contains("watch?v=")) {
                videoId = getQueryParam(originalUrl, "v");

            } else if (host != null && host.contains("youtu.be")) {
                videoId = path.substring(1); // /d2P8sVU5ydM -> d2P8sVU5ydM

            } else if (path.equals("/watch") && originalUrl.contains("v=")) {
                videoId = getQueryParam(originalUrl, "v");

            } else if (path.matches("^/@[\\w\\-]+$") ||
                    path.startsWith("/c/") ||
                    path.startsWith("/user/") ||
                    path.startsWith("/channel/")) {
                channelHandle = path.substring(1); // remove leading /
            }

        } catch (Exception e) {
            return fallbackUrl;
        }

        boolean isIOS = userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iOS");
        boolean isAndroid = userAgent.contains("Android");

        if (videoId != null) {
            String iosUrl = "youtube://watch?v=" + videoId;
            String androidUrl = "intent://scan/#Intent;" +
                    "scheme=vnd.youtube://watch?v=" + videoId + ";" +
                    "package=com.google.android.youtube;" +
                    "S.browser_fallback_url=" + fallbackUrl + ";" +
                    "end;";
            return isIOS ? iosUrl : isAndroid ? androidUrl : fallbackUrl;

        } else if (channelHandle != null) {
            String iosUrl = "youtube://youtube.com/" + channelHandle;
            String androidUrl = "vnd.youtube://" + channelHandle;
            return isIOS ? iosUrl : isAndroid ? androidUrl : fallbackUrl;
        }

        return fallbackUrl; // fallback if format not matched
    }

    private static String getQueryParam(String url, String key) {
        try {
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), Charset.forName("UTF-8"));
            for (NameValuePair param : params) {
                if (param.getName().equals(key)) {
                    return param.getValue();
                }
            }
        } catch (Exception ignored) {}
        return null;
    }



}
