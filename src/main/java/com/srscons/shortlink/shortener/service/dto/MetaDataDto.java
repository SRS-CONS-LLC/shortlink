package com.srscons.shortlink.shortener.service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataDto {
    private long userId;
    private String shortCode;
    private String ipAddress;
    private String country;
    private String os;
    private String userAgent;
    private String device;
    private String browser;
    private String referrer;
    private String language;
    private String timezone;
    private String smartlinkType;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private LocalDateTime timestamp;
}