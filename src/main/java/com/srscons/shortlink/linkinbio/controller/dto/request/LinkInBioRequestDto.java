package com.srscons.shortlink.linkinbio.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.srscons.shortlink.linkinbio.repository.entity.ThemeType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class LinkInBioRequestDto {

    private String title;
    private String description;
    @JsonIgnore
    private MultipartFile logoFile;
    private ThemeType themeType;
    private List<LinkItemRequestDto> links;

    public LinkInBioRequestDto() {
        this.links = new ArrayList<>();
        this.themeType = ThemeType.AUTO;
    }

    @Data
    public static class LinkItemRequestDto {
        private String title;
        private String url;
    }

}

