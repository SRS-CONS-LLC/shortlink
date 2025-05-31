package com.srscons.shortlink.analytics;

import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<MetaDataEntity, Long> {

    List<MetaDataEntity> findByShortLinkId(@Param("shortLinkId") Long shortLinkId);

    List<MetaDataEntity> findByShortLinkIdAndClickTimeBetween(
            @Param("shortLinkId") Long shortLinkId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Total clicks for a specific shortlink
    @Query("SELECT COUNT(m) FROM MetaDataEntity m WHERE m.shortLink.id = :shortLinkId")
    Long countClicksByShortLinkId(@Param("shortLinkId") Long shortLinkId);

    // Total QR code scans
    @Query("SELECT COUNT(m) FROM MetaDataEntity m WHERE m.shortLink.id = :shortLinkId AND m.channel = 'QR_CODE'")
    Long countQRCodeScansByShortLinkId(@Param("shortLinkId") Long shortLinkId);

    // Total referrals
    @Query("SELECT COUNT(m) FROM MetaDataEntity m WHERE m.shortLink.id = :shortLinkId AND m.referer IS NOT NULL")
    Long countReferralsByShortLinkId(@Param("shortLinkId") Long shortLinkId);

    // OS Chart Data - Get daily OS distribution
    @Query("SELECT DATE(m.clickTime) as clickDate, m.os as os, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "AND m.clickTime BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(m.clickTime), m.os " +
            "ORDER BY clickDate ASC")
    List<Object[]> getOSDistributionByDateRange(
            @Param("shortLinkId") Long shortLinkId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Device Analytics
    @Query("SELECT m.device as deviceType, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "GROUP BY m.device")
    List<Object[]> getDeviceAnalytics(@Param("shortLinkId") Long shortLinkId);

    // Countries Analytics
    @Query("SELECT m.country as country, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "GROUP BY m.country")
    List<Object[]> getCountriesAnalytics(@Param("shortLinkId") Long shortLinkId);

    // Referrers Analytics
    @Query("SELECT m.referer as referer, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "AND m.referer IS NOT NULL " +
            "GROUP BY m.referer")
    List<Object[]> getReferrersAnalytics(@Param("shortLinkId") Long shortLinkId);

    // Redirects Analytics
    @Query("SELECT m.redirectUrl as redirect, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "GROUP BY m.redirectUrl")
    List<Object[]> getRedirectsAnalytics(@Param("shortLinkId") Long shortLinkId);

    // UTM Analytics
    @Query("SELECT CONCAT(m.utmSource, ':', m.utmMedium, ':', m.utmCampaign) as utm, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "AND m.utmSource IS NOT NULL " +
            "GROUP BY m.utmSource, m.utmMedium, m.utmCampaign")
    List<Object[]> getUTMAnalytics(@Param("shortLinkId") Long shortLinkId);

    // Channels Analytics
    @Query("SELECT m.channel as channel, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "AND m.channel IS NOT NULL " +
            "GROUP BY m.channel")
    List<Object[]> getChannelsAnalytics(@Param("shortLinkId") Long shortLinkId);

    // Timezones Analytics
    @Query("SELECT m.timezone as timezone, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "AND m.timezone IS NOT NULL " +
            "GROUP BY m.timezone")
    List<Object[]> getTimezonesAnalytics(@Param("shortLinkId") Long shortLinkId);

    // Languages Analytics
    @Query("SELECT m.language as language, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "AND m.language IS NOT NULL " +
            "GROUP BY m.language")
    List<Object[]> getLanguagesAnalytics(@Param("shortLinkId") Long shortLinkId);

    // Browsers Analytics
    @Query("SELECT m.browser as browser, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "GROUP BY m.browser")
    List<Object[]> getBrowsersAnalytics(@Param("shortLinkId") Long shortLinkId);

    // Smartlink Types Analytics
    @Query("SELECT m.smartlinkType as type, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "AND m.smartlinkType IS NOT NULL " +
            "GROUP BY m.smartlinkType")
    List<Object[]> getSmartlinkTypesAnalytics(@Param("shortLinkId") Long shortLinkId);

    // Overall OS distribution
    @Query("SELECT m.os as os, COUNT(m) as count " +
            "FROM MetaDataEntity m " +
            "WHERE m.shortLink.id = :shortLinkId " +
            "GROUP BY m.os")
    List<Object[]> getOSDistribution(@Param("shortLinkId") Long shortLinkId);
}