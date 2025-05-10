package com.srscons.shortlink.linkinbio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Configuration
public class CloudflareConfig {

    @Value("${cloudflare.images.accountId}")
    private String accountId;

    @Value("${cloudflare.images.apiToken}")
    private String apiToken;

    @Value("${cloudflare.images.deliveryUrl}")
    private String deliveryUrl;

    @Bean
    public CloudflareProperties cloudflareProperties() {
        return new CloudflareProperties(accountId, apiToken, deliveryUrl);
    }
}