package com.srscons.shortlink.shortener.service;

import com.srscons.shortlink.common.exception.ShortLinkNotFoundException;
import com.srscons.shortlink.shortener.repository.MetaDataRepository;
import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import com.srscons.shortlink.shortener.service.dto.MetaDataDto;
import com.srscons.shortlink.shortener.service.mapper.MetaDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MetaDataService {
    private final MetaDataRepository metaDataRepository;
    private final MetaDataMapper metaDataMapper;

    @Transactional(readOnly = true)
    public Page<MetaDataDto> getAllMetaData(Pageable pageable) {
        return metaDataRepository.findAll(pageable)
                .map(metaDataMapper::fromEntityToDto);
    }

    @Transactional(readOnly = true)
    public Page<MetaDataDto> getMetaDataByShortCodeAndUserId(String shortCode, Long userId, Pageable pageable) {
        Page<MetaDataEntity> metadata = metaDataRepository.findByShortCodeAndUserId(shortCode, userId, pageable);
        if (metadata.isEmpty()) {
            throw new ShortLinkNotFoundException("No metadata found for short code: " + shortCode);
        }
        return metadata.map(metaDataMapper::fromEntityToDto);
    }

    @Transactional(readOnly = true)
    public MetaDataDto getMetaDataById(Long id) {
        return metaDataRepository.findById(id)
                .map(metaDataMapper::fromEntityToDto)
                .orElse(null);
    }
} 