package com.srscons.shortlink.linkinbio.service.mapper;


import com.srscons.shortlink.linkinbio.repository.entity.LinkInBioEntity;
import com.srscons.shortlink.linkinbio.repository.entity.LinkItemEntity;
import com.srscons.shortlink.linkinbio.service.dto.LinkInBioDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class LinkInBioMapper {

    public abstract LinkInBioEntity fromBusinessToEntity(LinkInBioDto dto);

    public abstract LinkInBioDto fromEntityToBusiness(LinkInBioEntity entity);

    public abstract List<LinkItemEntity> toEntityLinks(List<LinkInBioDto.LinkItemDto> dtos);

    public abstract List<LinkInBioDto.LinkItemDto> toDtoLinks(List<LinkItemEntity> entities);

    @AfterMapping
    protected void setBackReference(@MappingTarget LinkInBioEntity linkInBio) {
        if (linkInBio.getLinks() != null) {
            for (LinkItemEntity item : linkInBio.getLinks()) {
                item.setLinkInBio(linkInBio);
            }
        }
    }
}
