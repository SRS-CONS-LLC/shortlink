package com.srscons.shortlink.linkinbio.service.mapper;

import com.srscons.shortlink.linkinbio.repository.entity.LinkInBioEntity;
import com.srscons.shortlink.linkinbio.repository.entity.LinkItemEntity;
import com.srscons.shortlink.linkinbio.service.dto.LinkInBioDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class LinkInBioMapper {

    @Mapping(target = "logoUrl", source = "logoUrl")
    public abstract LinkInBioEntity fromBusinessToEntity(LinkInBioDto dto);

    @Mapping(target = "logoUrl", source = "logoUrl")
    public abstract LinkInBioDto fromEntityToBusiness(LinkInBioEntity entity);

    @Mapping(target = "logoUrl", source = "logoUrl")
    public abstract List<LinkItemEntity> toEntityLinks(List<LinkInBioDto.LinkItemDto> dtos);

    @Mapping(target = "logoUrl", source = "logoUrl")
    public abstract List<LinkInBioDto.LinkItemDto> toDtoLinks(List<LinkItemEntity> entities);

    @AfterMapping
    protected void setBackReference(@MappingTarget LinkInBioEntity linkInBio) {
        if (linkInBio.getLinks() != null) {
            for (LinkItemEntity item : linkInBio.getLinks()) {
                item.setLinkInBio(linkInBio);
            }
        }
    }

    @AfterMapping
    protected void mapId(@MappingTarget LinkInBioDto dto, LinkInBioEntity entity) {
        dto.setId(entity.getId());
    }
}
