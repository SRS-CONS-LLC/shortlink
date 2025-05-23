package com.srscons.shortlink.shortener.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
    // General Analytics
    private Long totalClicks;
    private Long totalQRCodeScans;
    private Long totalReferrals;

    // Device Analytics
    private Map<String, Long> devices; // Mobile, Desktop, Bot/Crawler counts

    // Countries Data
    private Map<String, Long> countries;

    // OS Distribution
    private Map<String, Long> operatingSystems;

    // Referrers Data
    private Map<String, Long> referrers;

    // Redirects Data
    private Map<String, Long> redirects;

    // UTM Data
    private Map<String, Long> utmParameters;

    // Channels Data
    private Map<String, Long> channels;

    // Timezones Data
    private Map<String, Long> timezones;

    // Languages Data
    private Map<String, Long> languages;

    // Browsers Data
    private Map<String, Long> browsers;

    // Smartlink Types
    private Map<String, Long> smartlinkTypes;

    // Shortened URLs
    private Map<String, Long> shortenedUrls;

    // Operating Systems Chart Data
    private List<OSChartData> osChartData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OSChartData {
        private String date;
        private Map<String, Long> osData; // OS name -> count for that date
    }
}