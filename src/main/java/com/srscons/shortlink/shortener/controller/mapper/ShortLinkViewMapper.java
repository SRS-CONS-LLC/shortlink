package com.srscons.shortlink.shortener.controller.mapper;

import com.srscons.shortlink.shortener.controller.dto.request.ShortLinkRequestDto;
import com.srscons.shortlink.shortener.controller.dto.response.ShortLinkResponseDto;
import com.srscons.shortlink.shortener.service.dto.ShortLinkDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring")
public interface ShortLinkViewMapper {
    @Mapping(target = "shortUrl", source = "shortCode", qualifiedByName = "toShortUrl")
    @Mapping(target = "links", source = "links")
    ShortLinkResponseDto fromBusinessToResponse(ShortLinkDto dto);

    ShortLinkDto fromRequestToBusiness(ShortLinkRequestDto request);

    @Named("toShortUrl")
    default String toShortUrl(String shortCode) {
        if (shortCode == null) {
            return null;
        }
        return "http://localhost:8080/" + shortCode;
    }
} 