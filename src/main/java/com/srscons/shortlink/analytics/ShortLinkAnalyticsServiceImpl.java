package com.srscons.shortlink.analytics;

import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShortLinkAnalyticsServiceImpl implements ShortLinkAnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final AnalyticsMapper analyticsMapper;

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDTO getAnalytics(Long shortLinkId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        
        List<MetaDataEntity> metaDataList;
        if (startDateTime != null && endDateTime != null) {
            metaDataList = analyticsRepository.findByShortLinkIdAndClickTimeBetween(
                shortLinkId, startDateTime, endDateTime);
        } else {
            metaDataList = analyticsRepository.findByShortLinkId(shortLinkId);
        }
        
        AnalyticsDTO analyticsDTO = analyticsMapper.toAnalytics(metaDataList);
        
        // Add OS chart data with date range
        if (startDateTime != null && endDateTime != null) {
            List<AnalyticsDTO.OSChartData> osChartData = getOSChartData(shortLinkId, startDateTime, endDateTime);
            analyticsDTO.setOsChartData(osChartData);
        }
        
        return analyticsDTO;
    }

    private List<AnalyticsDTO.OSChartData> getOSChartData(Long shortLinkId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> rawData = analyticsRepository.getOSDistributionByDateRange(shortLinkId, startDate, endDate);
        Map<String, Map<String, Long>> groupedByDate = new HashMap<>();

        for (Object[] row : rawData) {
            String date = row[0].toString();
            String os = row[1] != null ? row[1].toString() : "Unknown";
            Long count = (Long) row[2];

            groupedByDate.computeIfAbsent(date, k -> new HashMap<>()).put(os, count);
        }

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