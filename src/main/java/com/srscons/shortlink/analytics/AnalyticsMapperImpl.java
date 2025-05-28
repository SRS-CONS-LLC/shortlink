package com.srscons.shortlink.analytics;

import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Component
public class AnalyticsMapperImpl implements AnalyticsMapper {
    @Override
    public AnalyticsDTO toAnalytics(List<MetaDataEntity> metaDataList) {
        if (metaDataList == null) {
            return null;
        }

        return AnalyticsDTO.builder()
                .totalClicks((long) metaDataList.size())
                .totalQRCodeScans(countQRCodeScans(metaDataList))
                .totalReferrals(countReferrals(metaDataList))
                .devices(groupByDevice(metaDataList))
                .countries(groupByCountry(metaDataList))
                .operatingSystems(groupByOS(metaDataList))
                .browsers(groupByBrowser(metaDataList))
                .languages(groupByLanguage(metaDataList))
                .channels(groupByChannel(metaDataList))
                .referrers(groupByReferrer(metaDataList))
                .redirects(groupByRedirect(metaDataList))
                .utmParameters(groupByUTM(metaDataList))
                .timezones(groupByTimezone(metaDataList))
                .smartlinkTypes(groupBySmartlinkType(metaDataList))
                .shortenedUrls(groupByShortCode(metaDataList))
                .osChartData(getOSChartData(metaDataList))
                .build();
    }

    private Long countQRCodeScans(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .filter(data -> "QR_CODE".equals(data.getChannel()))
                .count();
    }

    private Long countReferrals(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .filter(data -> data.getReferer() != null && !data.getReferer().isEmpty())
                .count();
    }

    private Map<String, Long> groupByDevice(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .collect(Collectors.groupingBy(
                        meta -> meta.getDevice() != null ? meta.getDevice() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    private Map<String, Long> groupByCountry(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .collect(Collectors.groupingBy(
                        meta -> meta.getCountry() != null ? meta.getCountry() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    private Map<String, Long> groupByOS(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .collect(Collectors.groupingBy(
                        meta -> meta.getOs() != null ? meta.getOs() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    private Map<String, Long> groupByBrowser(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .collect(Collectors.groupingBy(
                        meta -> meta.getBrowser() != null ? meta.getBrowser() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    private Map<String, Long> groupByLanguage(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .collect(Collectors.groupingBy(
                        meta -> meta.getLanguage() != null ? meta.getLanguage() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    private Map<String, Long> groupByChannel(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .collect(Collectors.groupingBy(
                        meta -> meta.getChannel() != null ? meta.getChannel() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    private Map<String, Long> groupByReferrer(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .filter(meta -> meta.getReferer() != null)
                .collect(Collectors.groupingBy(
                        MetaDataEntity::getReferer,
                        Collectors.counting()
                ));
    }

    private Map<String, Long> groupByRedirect(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .filter(data -> data.getRedirectUrl() != null)
                .collect(Collectors.groupingBy(
                        MetaDataEntity::getRedirectUrl,
                        Collectors.counting()
                ));
    }

    private Map<String, Long> groupByUTM(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .collect(Collectors.groupingBy(
                        data -> String.format("%s:%s:%s",
                                data.getUtmSource() != null ? data.getUtmSource() : "UNKNOWN",
                                data.getUtmMedium() != null ? data.getUtmMedium() : "UNKNOWN",
                                data.getUtmCampaign() != null ? data.getUtmCampaign() : "UNKNOWN"),
                        Collectors.counting()
                ));
    }

    private Map<String, Long> groupByTimezone(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .collect(Collectors.groupingBy(
                        meta -> meta.getTimezone() != null ? meta.getTimezone() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    private Map<String, Long> groupBySmartlinkType(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .collect(Collectors.groupingBy(
                        meta -> meta.getSmartlinkType() != null ? meta.getSmartlinkType() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    private Map<String, Long> groupByShortCode(List<MetaDataEntity> metaDataList) {
        return metaDataList.stream()
                .collect(Collectors.groupingBy(
                        meta -> meta.getShortCode() != null ? meta.getShortCode() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    private List<AnalyticsDTO.OSChartData> getOSChartData(List<MetaDataEntity> metaDataList) {
        Map<String, Map<String, Long>> groupedByDate = metaDataList.stream()
                .collect(Collectors.groupingBy(
                        data -> data.getClickTime().toLocalDate().toString(),
                        Collectors.groupingBy(
                                data -> data.getOs() != null ? data.getOs() : "Unknown",
                                Collectors.counting()
                        )
                ));

        return groupedByDate.entrySet().stream()
                .map(entry -> AnalyticsDTO.OSChartData.builder()
                        .date(entry.getKey())
                        .osData(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(AnalyticsDTO.OSChartData::getDate))
                .collect(Collectors.toList());
    }

}