package com.srscons.shortlink.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class ShortLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String originalUrl;         // fallback URL
    private String youtubeUrl;
    private String instagramUrl;
    private String tiktokUrl;

    private String youtubeDeepLink;     // youtube://...
    private String instagramDeepLink;   // instagram://...
    private String tiktokDeepLink;      // tiktok://...
}
