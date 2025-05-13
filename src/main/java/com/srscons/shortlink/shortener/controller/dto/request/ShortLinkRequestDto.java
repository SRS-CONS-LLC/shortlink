package com.srscons.shortlink.shortener.controller.dto.request;

import com.srscons.shortlink.shortener.repository.entity.enums.LayoutType;
import com.srscons.shortlink.shortener.repository.entity.enums.ThemeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ShortLinkRequestDto {
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    private MultipartFile logoFile;

    private String originalUrl;

    @NotNull(message = "Theme type is required")
    private ThemeType themeType;

    @NotNull(message = "Layout type is required")
    private LayoutType layoutType;

    @Size(max = 7, message = "Theme color must be a valid hex color code (e.g., #RRGGBB)")
    private String themeColor;

    private List<LinkItemRequestDto> links;

    @Data
    public static class LinkItemRequestDto {
        @NotBlank(message = "Link title is required")
        @Size(max = 100, message = "Link title must be less than 100 characters")
        private String title;

        @NotBlank(message = "URL is required")
        @Size(max = 2048, message = "URL must be less than 2048 characters")
        private String url;

        private MultipartFile logoFile;
    }
} 