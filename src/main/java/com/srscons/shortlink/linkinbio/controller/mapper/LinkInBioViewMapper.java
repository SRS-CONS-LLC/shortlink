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

    @AfterMapping
    protected void mapMainLogo(@MappingTarget LinkInBioDto dto, LinkInBioRequestDto request) {
        // Map logo file if present
        dto.setLogoFile(request.getLogoFile());
        
        // Map logo URL if present
        if (request.getLogoUrl() != null) {
            dto.setLogoUrl(request.getLogoUrl());
        }
        
        // Handle logo removal
        if (Boolean.TRUE.equals(request.getRemoveLogo())) {
            dto.setLogoUrl(null);
        }
    }

    @AfterMapping
    protected void mapLinkLogoFiles(@MappingTarget LinkInBioDto dto, LinkInBioRequestDto request) {
        if (request.getLinks() != null && dto.getLinks() != null) {
            for (int i = 0; i < request.getLinks().size(); i++) {
                var requestLink = request.getLinks().get(i);
                var dtoLink = dto.getLinks().get(i);
                
                // Map logo file if present
                dtoLink.setLogoFile(requestLink.getLogoFile());
                
                // Map logo URL if present
                if (requestLink.getLogoUrl() != null) {
                    dtoLink.setLogoUrl(requestLink.getLogoUrl());
                }
                
                // Handle logo removal
                if (Boolean.TRUE.equals(requestLink.getRemoveLogo())) {
                    dtoLink.setLogoUrl(null);
                }
            }
        }
    }
}
