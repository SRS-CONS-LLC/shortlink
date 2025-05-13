package com.srscons.shortlink.tracker.service;

import com.srscons.shortlink.tracker.dto.ShortLinkResponse;
import com.srscons.shortlink.tracker.entity.ShortlinkEntity;
import com.srscons.shortlink.tracker.entity.MetaDataEntity;
import com.srscons.shortlink.tracker.repository.ShortLinkRepository;
import com.srscons.shortlink.tracker.repository.MetaDataRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShortLinkService {

    private final ShortLinkRepository shortLinkRepository;
    private final MetaDataRepository metaDataRepository;
    private static final Pattern OS_VERSION_PATTERN = Pattern.compile("(?:Windows NT|Mac OS X|Android|iOS)\\s*([\\d._]+)");
    private static final String ALPHABET = "23456789bcdfghjkmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 10;
    private final Random random = new SecureRandom();

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public ShortLinkResponse createShortLink(String originalUrl) {
        validateUrl(originalUrl);
        String shortCode = generateUniqueShortCode();
        
        ShortlinkEntity shortlinkEntity = ShortlinkEntity.builder()
                .shortCode(shortCode)
                .originalUrl(originalUrl)
                .build();
        shortLinkRepository.save(shortlinkEntity);
        
        return ShortLinkResponse.builder()
                .shortCode(shortCode)
                .shortUrl(baseUrl + "/" + shortCode)
                .originalUrl(originalUrl)
                .build();
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String shortCode) {
        return shortLinkRepository.findByShortCode(shortCode)
                .map(ShortlinkEntity::getOriginalUrl)
                .orElse(null);
    }

    @Transactional
    public void saveVisitMetadata(String shortCode, String redirectUrl, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String userAgentString = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        Browser browser = userAgent.getBrowser();
        OperatingSystem os = userAgent.getOperatingSystem();
        DeviceType deviceType = userAgent.getOperatingSystem().getDeviceType();

        // Parse query parameters
        Map<String, String> queryParams = parseQueryString(request.getQueryString());

        MetaDataEntity metaDataEntity = MetaDataEntity.builder()
                // Basic Information
                .shortCode(shortCode)
                .ipAddress(getClientIp(request))
                .referer(request.getHeader("Referer"))
                .channel(detectChannel(redirectUrl))
                .language(request.getHeader("Accept-Language"))
                .smartlinkType(detectSmartlinkType(redirectUrl))
                .country(request.getHeader("X-Country"))
                .redirectUrl(redirectUrl)
                
                // UTM Parameters
                .utmSource(request.getParameter("utm_source"))
                .utmMedium(queryParams.get("utm_medium"))
                .utmCampaign(queryParams.get("utm_campaign"))
                .utmTerm(queryParams.get("utm_term"))
                .utmContent(queryParams.get("utm_content"))
                
                // Technical Information
                .timezone(request.getHeader("X-Timezone"))
                .browser(browser.getName())
                .browserVersion(browser.getVersion(userAgentString).getVersion())
                .os(os.getName())
                .osVersion(detectOSVersion(userAgentString))
                .device(deviceType.getName())
                .deviceType(deviceType.toString())
                .screenResolution(request.getHeader("X-Screen-Resolution"))
                .colorDepth(request.getHeader("X-Color-Depth"))
                .isMobile(deviceType == DeviceType.MOBILE)
                .isTablet(deviceType == DeviceType.TABLET)
                .isDesktop(deviceType == DeviceType.COMPUTER)
                .isBot(userAgentString.toLowerCase().contains("bot"))
                
                // Network Information
                .connectionType(request.getHeader("X-Connection-Type"))
                .networkSpeed(request.getHeader("X-Network-Speed"))
                .proxy(detectProxy(request))
                .vpn(detectVPN(request))
                
                // Request Information
                .requestMethod(request.getMethod())
                .requestProtocol(request.getProtocol())
                .requestHost(request.getHeader("Host"))
                .requestPath(request.getRequestURI())
                .queryString(request.getQueryString())
                .clickTime(LocalDateTime.now())
                .responseTime((int) (System.currentTimeMillis() - startTime))
                
                // Additional Information
                .userAgent(userAgentString)
                .acceptLanguage(request.getHeader("Accept-Language"))
                .acceptEncoding(request.getHeader("Accept-Encoding"))
                .acceptCharset(request.getHeader("Accept-Charset"))
                .doNotTrack(request.getHeader("DNT"))
                .cookieEnabled(request.getHeader("X-Cookie-Enabled"))
                .javaEnabled(request.getHeader("X-Java-Enabled"))
                .flashEnabled(request.getHeader("X-Flash-Enabled"))
                .build();

        metaDataRepository.save(metaDataEntity);
    }

    private String generateUniqueShortCode() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String shortCode = generateShortCode();
            if (!shortLinkRepository.existsByShortCode(shortCode)) {
                return shortCode;
            }
        }
        throw new RuntimeException("Failed to generate unique short code after " + MAX_ATTEMPTS + " attempts");
    }

    private String generateShortCode() {
        StringBuilder shortCode = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            shortCode.append(ALPHABET.charAt(index));
        }
        return shortCode.toString();
    }

    private void validateUrl(String url) {
        try {
            new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format: " + url);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private Map<String, String> parseQueryString(String queryString) {
        if (!StringUtils.hasText(queryString)) {
            return Map.of();
        }
        return Arrays.stream(queryString.split("&"))
                .map(param -> param.split("="))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0],
                        parts -> parts[1]
                ));
    }

    private String detectProxy(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String proxyClientIp = request.getHeader("Proxy-Client-IP");
        String wlProxyClientIp = request.getHeader("WL-Proxy-Client-IP");
        
        return (StringUtils.hasText(forwardedFor) || StringUtils.hasText(proxyClientIp) || StringUtils.hasText(wlProxyClientIp)) 
                ? "Detected" : "Not Detected";
    }

    private String detectVPN(HttpServletRequest request) {
        // This is a simplified check. In production, you might want to use a more sophisticated VPN detection service
        String ip = getClientIp(request);
        // Add your VPN detection logic here
        return "Unknown";
    }

    private String detectChannel(String redirectUrl) {
        if (redirectUrl == null) return "Direct";
        if (redirectUrl.contains("facebook")) return "Facebook";
        if (redirectUrl.contains("youtube")) return "Youtube";
        if (redirectUrl.contains("instagram")) return "Instagram";
        if (redirectUrl.contains("tiktok")) return "TikTok";
        if (redirectUrl.contains("google")) return "Google";
        return "Other";
    }

    private String detectSmartlinkType(String redirectUrl) {
        if (redirectUrl.contains("youtube.com")) return "YouTube";
        if (redirectUrl.contains("google.com")) return "Google";
        if (redirectUrl.contains("facebook.com")) return "Facebook";
        if (redirectUrl.contains("tiktok.com")) return "TikTok";
        return "default";
    }

    private String detectOSVersion(String userAgent) {
        Matcher matcher = OS_VERSION_PATTERN.matcher(userAgent);
        if (matcher.find()) {
            String version = matcher.group(1);
            // Clean up version string
            return version.replace("_", ".");
        }
        return "Unknown";
    }
}
