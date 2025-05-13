package com.srscons.shortlink.shortener.controller.dto.response;

import com.srscons.shortlink.shortener.repository.entity.enums.LayoutType;
import com.srscons.shortlink.shortener.repository.entity.enums.ThemeType;
import lombok.Data;

import java.util.List;

@Data
public class ShortLinkResponseDto {
    private Long id;
    private String title;
    private String description;
    private String logoUrl;
    private ThemeType themeType;
    private LayoutType layoutType;
    private String themeColor;
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private List<LinkItemResponseDto> links;

    @Data
    public static class LinkItemResponseDto {
        private Long id;
        private String title;
        private String url;
        private String logoUrl;
    }
} 