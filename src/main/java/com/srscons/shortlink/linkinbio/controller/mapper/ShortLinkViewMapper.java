package com.srscons.shortlink.linkinbio.controller.mapper;

import com.srscons.shortlink.linkinbio.controller.dto.request.ShortLinkRequestDto;
import com.srscons.shortlink.linkinbio.controller.dto.response.ShortLinkResponseDto;
import com.srscons.shortlink.linkinbio.service.dto.ShortLinkDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShortLinkViewMapper {
    @Mapping(target = "shortUrl", ignore = true)
    ShortLinkResponseDto fromBusinessToResponse(ShortLinkDto dto);

    ShortLinkDto fromRequestToBusiness(ShortLinkRequestDto request);
} 