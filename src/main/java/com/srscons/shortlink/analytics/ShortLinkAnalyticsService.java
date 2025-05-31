package com.srscons.shortlink.analytics;

import java.time.LocalDate;

public interface ShortLinkAnalyticsService {
    AnalyticsDTO getAnalytics(Long shortLinkId, LocalDate startDate, LocalDate endDate);
}