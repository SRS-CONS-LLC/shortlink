package com.srscons.shortlink.linkinbio.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srscons.shortlink.linkinbio.repository.entity.LayoutType;
import com.srscons.shortlink.linkinbio.repository.entity.ThemeType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class LinkInBioResponseDto {

    private Long id;
    private String title;
    private String description;
    @JsonIgnore
    private MultipartFile logoFile;
    private ThemeType themeType;
    private LayoutType layoutType;
    private String themeColor;
    private List<LinkItemResponseDto> links;

    @Data
    public static class LinkItemResponseDto {
        private String title;
        private String url;
        @JsonIgnore
        private MultipartFile logoFile;
    }

}

