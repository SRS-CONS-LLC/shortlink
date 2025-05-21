package com.srscons.shortlink.shortener.config;

import com.srscons.shortlink.shortener.repository.entity.enums.LayoutType;
import com.srscons.shortlink.shortener.repository.entity.enums.ThemeType;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Add custom converter for ThemeType
        registry.addConverter(String.class, ThemeType.class, source -> {
            try {
                return ThemeType.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        });

        // Add custom converter for LayoutType
        registry.addConverter(String.class, LayoutType.class, source -> {
            try {
                return LayoutType.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
    }
} 