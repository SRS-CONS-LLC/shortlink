package com.srscons.shortlink.shortener.service.mapper;

import com.srscons.shortlink.shortener.repository.entity.ShortLinkEntity;
import com.srscons.shortlink.shortener.repository.entity.LinkItemEntity;
import com.srscons.shortlink.shortener.service.dto.ShortLinkDto;
import org.mapstruct.*;

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
    @Mapping(target = "qrCodeSvg", ignore = true)
    @Mapping(target = "visitMetadata", ignore = true)
    @Mapping(target = "linkType", source = "linkType")
    ShortLinkEntity fromBusinessToEntity(ShortLinkDto dto);

    @Mapping(target = "links", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shortCode", ignore = true)
    @Mapping(target = "qrCodeSvg", ignore = true)
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

    @AfterMapping
    default void mapLinksAfterCreation(ShortLinkDto dto, @MappingTarget ShortLinkEntity entity) {
        if (dto.getLinks() == null) return;

        List<LinkItemEntity> links = dto.getLinks().stream()
                .filter(l -> Boolean.FALSE.equals(l.getDeleted()))
                .map(l -> {
                    LinkItemEntity e = new LinkItemEntity();
                    e.setTitle(l.getTitle());
                    e.setUrl(l.getUrl());
                    e.setLogoUrl(l.getLogoUrl());
                    e.setDeleted(false);
                    e.setShortLink(entity);
                    return e;
                }).collect(Collectors.toList());

        entity.setLinks(links);
    }
} 