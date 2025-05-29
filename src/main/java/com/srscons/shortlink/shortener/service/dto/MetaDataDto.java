package com.srscons.shortlink.shortener.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataDto {
    private Long id;
    private String shortCode;
    private String ipAddress;
    private String referer;
    private String channel;
    private String language;
    private String smartlinkType;
    private String country;
    private String redirectUrl;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String utmTerm;
    private String utmContent;
    private String timezone;
    private String browser;
    private String browserVersion;
    private String os;
    private String osVersion;
    private String device;
    private String deviceType;
    private String screenResolution;
    private String colorDepth;
    private Boolean isMobile;
    private Boolean isTablet;
    private Boolean isDesktop;
    private Boolean isBot;
    private String connectionType;
    private String networkSpeed;
    private String proxy;
    private String vpn;
    private String requestMethod;
    private String requestProtocol;
    private String requestHost;
    private String requestPath;
    private String queryString;
    private LocalDateTime clickTime;
    private Integer responseTime;
    private String userAgent;
    private String acceptLanguage;
    private String acceptEncoding;
    private String acceptCharset;
    private String doNotTrack;
    private String cookieEnabled;
    private String javaEnabled;
    private String flashEnabled;
    private LocalDateTime createdAt;
} 