package com.srscons.shortlink.shortener.analytics;

import com.srscons.shortlink.shortener.repository.entity.ShortLinkEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "meta_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "short_link_id")
    private ShortLinkEntity shortLink;

    @Column(name = "short_code")
    private String shortCode;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "referer")
    private String referer;

    @Column(name = "channel")
    private String channel;

    @Column(name = "language")
    private String language;

    @Column(name = "smartlink_type")
    private String smartlinkType;

    @Column(name = "country")
    private String country;

    @Column(name = "redirect_url")
    private String redirectUrl;

    // UTM Parameters
    @Column(name = "utm_source")
    private String utmSource;

    @Column(name = "utm_medium")
    private String utmMedium;

    @Column(name = "utm_campaign")
    private String utmCampaign;

    @Column(name = "utm_term")
    private String utmTerm;

    @Column(name = "utm_content")
    private String utmContent;

    // Technical Information
    @Column(name = "timezone")
    private String timezone;

    @Column(name = "browser")
    private String browser;

    @Column(name = "browser_version")
    private String browserVersion;

    @Column(name = "os")
    private String os;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "device")
    private String device;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "screen_resolution")
    private String screenResolution;

    @Column(name = "color_depth")
    private String colorDepth;

    @Column(name = "is_mobile")
    private Boolean isMobile;

    @Column(name = "is_tablet")
    private Boolean isTablet;

    @Column(name = "is_desktop")
    private Boolean isDesktop;

    @Column(name = "is_bot")
    private Boolean isBot;

    // Network Information
    @Column(name = "connection_type")
    private String connectionType;

    @Column(name = "network_speed")
    private String networkSpeed;

    @Column(name = "proxy")
    private String proxy;

    @Column(name = "vpn")
    private String vpn;

    // Request Information
    @Column(name = "request_method")
    private String requestMethod;

    @Column(name = "request_protocol")
    private String requestProtocol;

    @Column(name = "request_host")
    private String requestHost;

    @Column(name = "request_path")
    private String requestPath;

    @Column(name = "query_string")
    private String queryString;

    @Column(name = "click_time")
    private LocalDateTime clickTime;

    @Column(name = "response_time")
    private Integer responseTime;

    // Additional Information
    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "accept_language")
    private String acceptLanguage;

    @Column(name = "accept_encoding")
    private String acceptEncoding;

    @Column(name = "accept_charset")
    private String acceptCharset;

    @Column(name = "do_not_track")
    private String doNotTrack;

    @Column(name = "cookie_enabled")
    private String cookieEnabled;

    @Column(name = "java_enabled")
    private String javaEnabled;

    @Column(name = "flash_enabled")
    private String flashEnabled;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 