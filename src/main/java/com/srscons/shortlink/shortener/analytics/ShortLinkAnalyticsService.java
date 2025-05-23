package com.srscons.shortlink.shortener.analytics;

public interface ShortLinkAnalyticsService {
    AnalyticsDTO getAnalyticsByShortLinkId(Long shortLinkId);
}