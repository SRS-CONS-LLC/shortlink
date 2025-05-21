package com.srscons.shortlink.shortener.controller.mapper;

import com.srscons.shortlink.shortener.controller.dto.request.ShortLinkRequestDto;
import com.srscons.shortlink.shortener.controller.dto.response.ShortLinkResponseDto;
import com.srscons.shortlink.shortener.service.dto.ShortLinkDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShortLinkViewMapper {
    @Mapping(target = "shortUrl", ignore = true)
    ShortLinkResponseDto fromBusinessToResponse(ShortLinkDto dto);

    ShortLinkDto fromRequestToBusiness(ShortLinkRequestDto request);
} 