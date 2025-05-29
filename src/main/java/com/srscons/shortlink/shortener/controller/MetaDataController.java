package com.srscons.shortlink.shortener.controller;

import com.srscons.shortlink.auth.entity.UserEntity;
import com.srscons.shortlink.auth.util.CONSTANTS;
import com.srscons.shortlink.shortener.service.MetaDataService;
import com.srscons.shortlink.shortener.service.dto.MetaDataDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.DeviceType;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/metadata")
@RequiredArgsConstructor
public class MetaDataController {
    private final MetaDataService metaDataService;

    @GetMapping
    public ResponseEntity<Page<MetaDataDto>> getAllMetaData(Pageable pageable) {
        return ResponseEntity.ok(metaDataService.getAllMetaData(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaDataDto> getMetaDataById(@PathVariable Long id) {
        MetaDataDto metadata = metaDataService.getMetaDataById(id);
        if (metadata == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/shortcode/{shortCode}")
    public ResponseEntity<Page<MetaDataDto>> getMetaDataByShortCode(
            @PathVariable String shortCode,
            Pageable pageable,
            HttpServletRequest request) {
        UserEntity loggedInUser = (UserEntity) request.getAttribute(CONSTANTS.LOGGED_IN_USER);
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(metaDataService.getMetaDataByShortCodeAndUserId(shortCode, loggedInUser.getId(), pageable));
    }

    @PostMapping("/collect")
    public ResponseEntity<Void> collectMetadata(HttpServletRequest request, @RequestParam String shortCode) {
        String ipAddress = request.getRemoteAddr();
        String userAgentHeader = request.getHeader("User-Agent");
        String referrer = request.getHeader("Referer");
        String language = request.getHeader("Accept-Language");
        String timezone = request.getHeader("Time-Zone");
        String smartlinkType = request.getParameter("smartlink_type");
        String utmSource = request.getParameter("utm_source");
        String utmMedium = request.getParameter("utm_medium");
        String utmCampaign = request.getParameter("utm_campaign");

        // UserAgent parsing
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentHeader);
        Browser browser = userAgent.getBrowser();
        OperatingSystem os = userAgent.getOperatingSystem();
        DeviceType deviceType = os.getDeviceType();

        MetaDataDto metaDataDto = MetaDataDto.builder()
                .shortCode(shortCode)
                .ipAddress(ipAddress)
                .browser(browser != null ? browser.getName() : null)
                .os(os != null ? os.getName() : null)
                .device(deviceType != null ? deviceType.getName() : null)
                .referrer(referrer)
                .language(language)
                .timezone(timezone)
                .smartlinkType(smartlinkType)
                .utmSource(utmSource)
                .utmMedium(utmMedium)
                .utmCampaign(utmCampaign)
                .userAgent(userAgentHeader)
                .timestamp(LocalDateTime.now())
                .build();

        metaDataService.collectMetaData(metaDataDto, null);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}