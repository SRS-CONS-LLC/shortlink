package com.srscons.shortlink.linkinbio.controller.mapper;

import com.srscons.shortlink.linkinbio.controller.dto.request.LinkInBioRequestDto;
import com.srscons.shortlink.linkinbio.controller.dto.response.LinkInBioResponseDto;
import com.srscons.shortlink.linkinbio.service.dto.LinkInBioDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class LinkInBioViewMapper {

    public abstract LinkInBioDto fromRequestToBusiness(LinkInBioRequestDto dto);

    public abstract LinkInBioResponseDto fromBusinessToResponse(LinkInBioDto dto);

    @AfterMapping
    protected void mapId(@MappingTarget LinkInBioResponseDto responseDto, LinkInBioDto dto) {
        responseDto.setId(dto.getId());
    }
}
