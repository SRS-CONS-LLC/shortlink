package com.srscons.shortlink.VisitLog.Service;

import com.srscons.shortlink.VisitLog.Entity.MetaDataEntity;
import com.srscons.shortlink.VisitLog.Repository.MetaDataRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MetaDataService {

    private final MetaDataRepository metaDataRepository;


    public void saveVisit(String shortCode, String redirectUrl, HttpServletRequest request, String timezone, String utmSource ,String country) {

        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        String language = request.getHeader("Accept-Language");

        MetaDataEntity metaDataEntity = MetaDataEntity.builder()
                .shortCode(shortCode)
                .ipAddress(ip)
                .referer(referer)
                .channel(detectChannel(redirectUrl))
                .language(language)
                .smartlinkType(detectSmartlinkType(redirectUrl))
                .country(country) // JS-dən gəlməzsə IP ilə
                .redirectUrl(redirectUrl)
                .utmSource(utmSource)
                .timezone(timezone)
                .browser(detectBrowser(ua))
                .os(detectOS(ua))
                .device(detectDevice(ua))
                .clickTime(LocalDateTime.now())
                .build();

        metaDataRepository.save(metaDataEntity);
    }

    private String detectChannel(String redirectUrl) {
        if (redirectUrl == null) return "Direct";
        if (redirectUrl.contains("facebook")) return "Facebook";
        if (redirectUrl.contains("youtube"))  return "Youtube";
        if (redirectUrl.contains("instagram")) return "Instagram";
        if (redirectUrl.contains("tiktok")) return "TikTok";
        if (redirectUrl.contains("google")) return "Google";
        return "Other";
    }

    private String detectSmartlinkType(String originalUrl) {
        if (originalUrl.contains("youtube.com")) return "YouTube";
        if (originalUrl.contains("google.com")) return "Google";
        if (originalUrl.contains("facebook.com")) return "Facebook";
        if (originalUrl.contains("tiktok.com")) return "TikTok";
        return "default";
    }

    private String detectBrowser(String ua) {
        ua = ua.toLowerCase();
        if (ua.contains("chrome")) return "Chrome";
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("safari") && !ua.contains("chrome")) return "Safari";
        if (ua.contains("edge")) return "Edge";
        return "Unknown";
    }

    private String detectOS(String ua) {
        ua = ua.toLowerCase();
        if (ua.contains("windows")) return "Windows";
        if (ua.contains("mac")) return "MacOS";
        if (ua.contains("android")) return "Android";
        if (ua.contains("iphone") || ua.contains("ios")) return "iOS";
        return "Unknown";
    }

    private String detectDevice(String ua) {
        ua = ua.toLowerCase();
        if (ua.contains("mobile")) return "Mobile";
        if (ua.contains("tablet")) return "Tablet";
        return "Desktop";
    }


}

