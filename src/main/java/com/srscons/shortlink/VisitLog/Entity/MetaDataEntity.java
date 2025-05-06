package com.srscons.shortlink.VisitLog.Entity;

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

    private String shortCode;           // shortened URLs
    private String ipAddress;
    private String referer;            // Referrers
    private String channel;            // Channels
    private String language;           // Languages
    private String smartlinkType;      // Smartlink Type
    private String country;            // Country
    private String redirectUrl;        // Redirects
    private String utmSource;          // UTM

    private String timezone;           // Timezones
    private String browser;            // Browsers
    private String os;                 // OS
    private String device;             // Devices
    private LocalDateTime clickTime;
}
