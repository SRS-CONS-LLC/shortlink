package com.srscons.shortlink.shortener.service;

import com.srscons.shortlink.shortener.repository.MetaDataRepository;
import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import com.srscons.shortlink.shortener.service.dto.MetaDataDto;
import com.srscons.shortlink.shortener.service.mapper.MetaDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetaDataService {
    private final MetaDataRepository repository;
    private final MetaDataMapper mapper;

    public void collectMetaData(MetaDataDto dto, Long userId) {
        MetaDataEntity entity = mapper.toEntity(dto);
        repository.save(entity);
    }

    public Page<MetaDataDto> getAllMetaData(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    public MetaDataDto getMetaDataById(Long id) {
        return repository.findById(id).map(mapper::toDto).orElse(null);
    }

    public Page<MetaDataDto> getMetaDataByShortCodeAndUserId(String shortCode, Long userId, Pageable pageable) {
        return repository.findAllByShortCodeAndUserId(shortCode, userId, pageable).map(mapper::toDto);
    }
}