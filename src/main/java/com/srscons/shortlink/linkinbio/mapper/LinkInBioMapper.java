package com.srscons.shortlink.linkinbio.mapper;


import com.srscons.shortlink.linkinbio.dto.LinkInBioDto;
import com.srscons.shortlink.linkinbio.dto.LinkItemDto;
import com.srscons.shortlink.linkinbio.entity.LinkInBio;
import com.srscons.shortlink.linkinbio.entity.LinkItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class LinkInBioMapper {

    public abstract LinkInBio toEntity(LinkInBioDto dto);

    public abstract LinkInBioDto toDto(LinkInBio entity);

    public abstract List<LinkItem> toEntityLinks(List<LinkItemDto> dtos);

    public abstract List<LinkItemDto> toDtoLinks(List<LinkItem> entities);

    @AfterMapping
    protected void setBackReference(@MappingTarget LinkInBio linkInBio) {
        if (linkInBio.getLinks() != null) {
            for (LinkItem item : linkInBio.getLinks()) {
                item.setLinkInBio(linkInBio);
            }
        }
    }
}
