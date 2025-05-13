package com.srscons.shortlink.shortener.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CloudflareProperties {
    private String accountId;
    private String apiToken;
    private String deliveryUrl;
}