package com.srscons.shortlink.linkinbio.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srscons.shortlink.linkinbio.repository.entity.LayoutType;
import com.srscons.shortlink.linkinbio.repository.entity.ThemeType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class LinkInBioRequestDto {

    private Long id;
    private String title;
    private String description;

    @JsonIgnore
    private MultipartFile logoFile;
    private String logoUrl;
    private ThemeType themeType;
    private LayoutType layoutType;
    private String themeColor;
    private Boolean removeLogo;

    private List<LinkItemResponseDto> links;

    @Data
    public static class LinkItemResponseDto {
        private String title;
        private String url;
        @JsonIgnore
        private MultipartFile logoFile;
        private String logoUrl;
        private Boolean removeLogo;
    }

}

