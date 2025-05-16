package com.srscons.shortlink.shortener.service.mapper;

import com.srscons.shortlink.shortener.repository.entity.ShortLinkEntity;
import com.srscons.shortlink.shortener.repository.entity.LinkItemEntity;
import com.srscons.shortlink.shortener.service.dto.ShortLinkDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ShortLinkMapper {
    @Mapping(target = "links", source = "links", qualifiedByName = "mapLinks")
    @Mapping(target = "linkType", source = "linkType")
    ShortLinkDto fromEntityToBusiness(ShortLinkEntity entity);

    @Mapping(target = "links", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shortCode", ignore = true)
    @Mapping(target = "visitMetadata", ignore = true)
    @Mapping(target = "linkType", source = "linkType")
    ShortLinkEntity fromBusinessToEntity(ShortLinkDto dto);

    @Mapping(target = "links", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shortCode", ignore = true)
    @Mapping(target = "visitMetadata", ignore = true)
    void updateEntityFromDto(ShortLinkDto dto, @MappingTarget ShortLinkEntity entity);

    @Named("mapLinks")
    default List<ShortLinkDto.LinkItemDto> mapLinks(List<LinkItemEntity> links) {
        if (links == null) {
            return null;
        }
        return links.stream()
                .filter(link -> !link.isDeleted())
                .map(link -> {
                    ShortLinkDto.LinkItemDto dto = new ShortLinkDto.LinkItemDto();
                    dto.setId(link.getId());
                    dto.setTitle(link.getTitle());
                    dto.setUrl(link.getUrl());
                    dto.setLogoUrl(link.getLogoUrl());
                    dto.setDeleted(link.isDeleted());
                    return dto;
                })
                .collect(Collectors.toList());
    }
} 