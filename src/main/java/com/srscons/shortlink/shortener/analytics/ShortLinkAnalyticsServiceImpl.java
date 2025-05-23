package com.srscons.shortlink.shortener.analytics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShortLinkAnalyticsServiceImpl implements ShortLinkAnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDTO getAnalyticsByShortLinkId(Long shortLinkId) {
        // Get the date range for OS chart data (last 7 days)
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7);

        // Get all required analytics data
        Long totalClicks = analyticsRepository.countClicksByShortLinkId(shortLinkId);
        Long totalQRCodeScans = analyticsRepository.countQRCodeScansByShortLinkId(shortLinkId);
        Long totalReferrals = analyticsRepository.countReferralsByShortLinkId(shortLinkId);

        // Get all analytics data
        List<AnalyticsDTO.OSChartData> osChartData = getOSChartData(shortLinkId, startDate, endDate);
        Map<String, Long> devices = convertToMap(analyticsRepository.getDeviceAnalytics(shortLinkId));
        Map<String, Long> countries = convertToMap(analyticsRepository.getCountriesAnalytics(shortLinkId));
        Map<String, Long> operatingSystems = convertToMap(analyticsRepository.getOSDistributionByDateRange(shortLinkId, startDate, endDate));
        Map<String, Long> referrers = convertToMap(analyticsRepository.getReferrersAnalytics(shortLinkId));
        Map<String, Long> redirects = convertToMap(analyticsRepository.getRedirectsAnalytics(shortLinkId));
        Map<String, Long> utmParameters = convertToMap(analyticsRepository.getUTMAnalytics(shortLinkId));
        Map<String, Long> channels = convertToMap(analyticsRepository.getChannelsAnalytics(shortLinkId));
        Map<String, Long> timezones = convertToMap(analyticsRepository.getTimezonesAnalytics(shortLinkId));
        Map<String, Long> languages = convertToMap(analyticsRepository.getLanguagesAnalytics(shortLinkId));
        Map<String, Long> browsers = convertToMap(analyticsRepository.getBrowsersAnalytics(shortLinkId));
        Map<String, Long> smartlinkTypes = convertToMap(analyticsRepository.getSmartlinkTypesAnalytics(shortLinkId));

        // Build and return the DTO
        return AnalyticsDTO.builder()
                .totalClicks(totalClicks)
                .totalQRCodeScans(totalQRCodeScans)
                .totalReferrals(totalReferrals)
                .osChartData(osChartData)
                .devices(devices)
                .countries(countries)
                .operatingSystems(operatingSystems)
                .referrers(referrers)
                .redirects(redirects)
                .utmParameters(utmParameters)
                .channels(channels)
                .timezones(timezones)
                .languages(languages)
                .browsers(browsers)
                .smartlinkTypes(smartlinkTypes)
                .build();
    }

    private List<AnalyticsDTO.OSChartData> getOSChartData(Long shortLinkId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> rawData = analyticsRepository.getOSDistributionByDateRange(shortLinkId, startDate, endDate);
        Map<String, Map<String, Long>> groupedByDate = new HashMap<>();

        // Process raw data and group by date
        for (Object[] row : rawData) {
            String date = row[0].toString();
            String os = row[1] != null ? row[1].toString() : "Unknown";
            Long count = (Long) row[2];

            groupedByDate.computeIfAbsent(date, k -> new HashMap<>()).put(os, count);
        }

        // Convert grouped data to DTO format
        List<AnalyticsDTO.OSChartData> chartDataList = new ArrayList<>();
        groupedByDate.forEach((date, osData) -> {
            AnalyticsDTO.OSChartData chartData = AnalyticsDTO.OSChartData.builder()
                    .date(date)
                    .osData(osData)
                    .build();
            chartDataList.add(chartData);
        });

        return chartDataList;
    }

    private Map<String, Long> convertToMap(List<Object[]> data) {
        Map<String, Long> result = new HashMap<>();
        if (data != null) {
            for (Object[] row : data) {
                if (row[0] != null) {
                    result.put(row[0].toString(), (Long) row[1]);
                }
            }
        }
        return result;
    }
} 