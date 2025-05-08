package com.srscons.shortlink.linkinbio.controller.mapper;

import com.srscons.shortlink.linkinbio.controller.dto.request.LinkInBioRequestDto;
import com.srscons.shortlink.linkinbio.controller.dto.response.LinkInBioResponseDto;
import com.srscons.shortlink.linkinbio.service.dto.LinkInBioDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring")
public abstract class LinkInBioViewMapper {

    @Value("${file.upload-dir-view}")
    private String uploadDirView;

    public abstract LinkInBioDto fromRequestToBusiness(LinkInBioRequestDto dto);

    @Mapping(target = "logoFileName", source = "logoFileName")
    public abstract LinkInBioResponseDto fromBusinessToResponse(LinkInBioDto dto);

    @AfterMapping
    protected void mapId(@MappingTarget LinkInBioResponseDto responseDto, LinkInBioDto dto) {
        responseDto.setId(dto.getId());
        
        // Prepend uploadDirView to logo file names
        if (dto.getLogoFileName() != null) {
            responseDto.setLogoFileName(uploadDirView + "/" + dto.getLogoFileName());
        }
        
        if (dto.getLinks() != null) {
            for (LinkInBioResponseDto.LinkItemResponseDto linkItemDto : responseDto.getLinks()) {
                if (linkItemDto.getLogoFileName() != null) {
                    linkItemDto.setLogoFileName(uploadDirView + "/" + linkItemDto.getLogoFileName());
                }
            }
        }
    }
}
