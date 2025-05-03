package com.srscons.shortlink.service.impl;

import com.srscons.shortlink.dto.SmartlinkRequestDto;
import com.srscons.shortlink.dto.SmartlinkResponseDto;
import com.srscons.shortlink.model.Smartlink;
import com.srscons.shortlink.repository.SmartLinkRepository;
import com.srscons.shortlink.service.SmartlinkService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SmartlinkServiceImpl implements SmartlinkService {
    private final SmartLinkRepository smartlinkRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<SmartlinkResponseDto> getAllSmartlinks() {
        return smartlinkRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SmartlinkResponseDto> getDraftSmartlinks() {
        return smartlinkRepository.findByDraft(true)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SmartlinkResponseDto create(SmartlinkRequestDto dto) {
        Smartlink smartlink = modelMapper.map(dto, Smartlink.class);
        smartlink.setCreatedAt(LocalDateTime.now());
        Smartlink saved = smartlinkRepository.save(smartlink);
        return convertToDto(saved);
    }

    private SmartlinkResponseDto convertToDto(Smartlink entity) {
        return SmartlinkResponseDto.builder()
//                .id(entity.getId())
                .title(entity.getTitle())
                .url(entity.getUrl())
                .draft(entity.isDraft())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}






