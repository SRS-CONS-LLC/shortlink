package com.srscons.shortlink.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkResponse {
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
} 