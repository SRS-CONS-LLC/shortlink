package com.srscons.shortlink.shortener.service.mapper;

import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import com.srscons.shortlink.shortener.service.dto.MetaDataDto;
import org.springframework.stereotype.Component;

@Component
public class MetaDataMapper {
    public MetaDataEntity toEntity(MetaDataDto dto) {
        return MetaDataEntity.builder()
                .shortCode(dto.getShortCode())
                .ipAddress(dto.getIpAddress())
                .country(dto.getCountry())
                .os(dto.getOs())
                .userId(dto.getUserId())
                .deviceType(dto.getDevice())
                .browser(dto.getBrowser())
                .referrer(dto.getReferrer())
                .language(dto.getLanguage())
                .timezone(dto.getTimezone())
                .smartlinkType(dto.getSmartlinkType())
                .utmSource(dto.getUtmSource())
                .utmMedium(dto.getUtmMedium())
                .utmCampaign(dto.getUtmCampaign())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public MetaDataDto toDto(MetaDataEntity entity) {
        return MetaDataDto.builder()
                .shortCode(entity.getShortCode())
                .ipAddress(entity.getIpAddress())
                .country(entity.getCountry())
                .os(entity.getOs())
                .userId(entity.getUserId())
                .device(entity.getDeviceType())
                .browser(entity.getBrowser())
                .referrer(entity.getReferrer())
                .language(entity.getLanguage())
                .timezone(entity.getTimezone())
                .smartlinkType(entity.getSmartlinkType())
                .utmSource(entity.getUtmSource())
                .utmMedium(entity.getUtmMedium())
                .utmCampaign(entity.getUtmCampaign())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
