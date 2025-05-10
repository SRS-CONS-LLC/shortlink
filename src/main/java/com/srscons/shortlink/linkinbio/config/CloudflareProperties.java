package com.srscons.shortlink.linkinbio.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CloudflareProperties {
    private String accountId;
    private String apiToken;
    private String deliveryUrl;
}