package com.srscons.shortlink.tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "metadata_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetaDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortCode;
    private String ipAddress;
    private String referer;
    private String channel;
    private String language;
    private String smartlinkType;
    private String country;
    private String redirectUrl;
    
    // UTM Parameters
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String utmTerm;
    private String utmContent;

    // Technical Information
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
    
    // Network Information
    private String connectionType;
    private String networkSpeed;
    private String proxy;
    private String vpn;
    
    // Request Information
    private String requestMethod;
    private String requestProtocol;
    private String requestHost;
    private String requestPath;
    private String queryString;
    private LocalDateTime clickTime;
    private Integer responseTime;
    
    // Additional Information
    private String userAgent;
    private String acceptLanguage;
    private String acceptEncoding;
    private String acceptCharset;
    private String doNotTrack;
    private String cookieEnabled;
    private String javaEnabled;
    private String flashEnabled;
}
