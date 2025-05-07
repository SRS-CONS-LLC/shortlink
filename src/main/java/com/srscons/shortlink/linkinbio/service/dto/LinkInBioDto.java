package com.srscons.shortlink.linkinbio.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srscons.shortlink.linkinbio.repository.entity.ThemeType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class LinkInBioDto {
    private String title;
    private String description;
    @JsonIgnore
    private MultipartFile logoFile;
    private ThemeType themeType;
    private List<LinkItemDto> links;


    @Data
    public static class LinkItemDto {
        private String title;
        private String url;
    }
}

