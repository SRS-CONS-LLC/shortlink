package com.srscons.shortlink.linkinbio.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srscons.shortlink.linkinbio.repository.entity.LayoutType;
import com.srscons.shortlink.linkinbio.repository.entity.ThemeType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class LinkInBioDto {
    private Long id;
    private String title;
    private String description;
    private String logoFileName;
    private ThemeType themeType;
    private LayoutType layoutType;
    private String themeColor;
    private List<LinkItemDto> links;

    @JsonIgnore
    private MultipartFile logoFile;
    private String logoUrl;
    @Data
    public static class LinkItemDto {
        private String title;
        private String url;
        @JsonIgnore
        private MultipartFile logoFile;

        private String logoUrl;
    }
}

