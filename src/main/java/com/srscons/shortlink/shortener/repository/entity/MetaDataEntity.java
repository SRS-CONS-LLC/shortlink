package com.srscons.shortlink.shortener.repository.entity;

import jakarta.persistence.*;
import lombok.*;
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

    private Long userId;

    @Column(name = "short_code", nullable = false, length = 100)
    private String shortCode;

    @Column(name = "ip_address", length = 45) // IPv6 max length is 45 chars
    private String ipAddress;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String os;

    @Column(name = "device_type", length = 100)
    private String deviceType;

    @Column(length = 100)
    private String browser;

    @Column(length = 255)
    private String referrer;

    @Column(length = 50)
    private String language;

    @Column(length = 50)
    private String timezone;

    @Column(name = "smartlink_type", length = 50)
    private String smartlinkType;

    @Column(name = "utm_source", length = 100)
    private String utmSource;

    @Column(name = "utm_medium", length = 100)
    private String utmMedium;

    @Column(name = "utm_campaign", length = 100)
    private String utmCampaign;

    private LocalDateTime timestamp;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "short_link_id")
    private ShortLinkEntity shortLink;
}
