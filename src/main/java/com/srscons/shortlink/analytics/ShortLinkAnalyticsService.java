package com.srscons.shortlink.analytics;

import java.time.LocalDate;

public interface ShortLinkAnalyticsService {
    /**
     * Get complete analytics data for a specific shortlink
     * @param shortLinkId ID of the shortlink
     * @param startDate Start date for the analytics data
     * @param endDate End date for the analytics data
     * @return Complete analytics data
     */
    AnalyticsDTO getAnalytics(Long shortLinkId, LocalDate startDate, LocalDate endDate);
}