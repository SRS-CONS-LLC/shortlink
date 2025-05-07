package com.srscons.shortlink.linkinbio.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srscons.shortlink.linkinbio.entity.ThemeType;
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
}

