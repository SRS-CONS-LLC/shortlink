package com.srscons.shortlink.analytics;

public interface ShortLinkAnalyticsService {
    /**
     * Get complete analytics data for a specific shortlink
     * @param shortLinkId ID of the shortlink
     * @return Complete analytics data
     */
    AnalyticsDTO getAnalytics(Long shortLinkId);
}