package com.srscons.shortlink.shortener.service.dto;

import com.srscons.shortlink.shortener.repository.entity.enums.LayoutType;
import com.srscons.shortlink.shortener.repository.entity.enums.ThemeType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ShortLinkDto {
    
    private Long id;
    private String title;
    private String description;
    private String logoUrl;
    private MultipartFile logoFile;
    private ThemeType themeType;
    private LayoutType layoutType;
    private String themeColor;
    private String shortCode;
    private String originalUrl;
    private List<LinkItemDto> links;
    private boolean deleted = false;

    @Data
    public static class LinkItemDto {
        private Long id;
        private String title;
        private String url;
        private String logoUrl;
        private MultipartFile logoFile;
    }
} 