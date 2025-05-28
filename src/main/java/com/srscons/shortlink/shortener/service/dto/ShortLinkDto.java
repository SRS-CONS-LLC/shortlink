package com.srscons.shortlink.shortener.service.dto;

import com.srscons.shortlink.shortener.repository.entity.enums.LayoutType;
import com.srscons.shortlink.shortener.repository.entity.enums.LinkType;
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
    private LinkType linkType;
    private String themeColor;
    private String shortCode;
    private String qrCodeSvg;
    private String originalUrl;
    private List<LinkItemDto> links;
    private UserDto user;
    private boolean deleted = false;
    private boolean removeMainLogo;

    @Data
    public static class LinkItemDto {
        private Long id;
        private String title;
        private String url;
        private String logoUrl;
        private MultipartFile logoFile;
        private boolean removeLogo;
        private Boolean deleted;
    }
} 