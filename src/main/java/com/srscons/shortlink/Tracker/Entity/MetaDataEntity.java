package com.srscons.shortlink.Tracker.Entity;

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
    private String utmSource;

    private String timezone;
    private String browser;
    private String os;
    private String device;
    private LocalDateTime clickTime;
}
