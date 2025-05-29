package com.srscons.shortlink.shortener.repository.entity;

import com.srscons.shortlink.auth.entity.UserEntity;
import com.srscons.shortlink.shortener.repository.entity.enums.LayoutType;
import com.srscons.shortlink.shortener.repository.entity.enums.LinkType;
import com.srscons.shortlink.shortener.repository.entity.enums.ThemeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "short_link")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ShortLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "short_code", unique = true, nullable = false)
    private String shortCode;

    @Column(name = "qr_code_svg", columnDefinition = "TEXT")
    private String qrCodeSvg;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme_type", columnDefinition = "varchar(10) default 'AUTO'")
    private ThemeType themeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "layout_type", columnDefinition = "varchar(10) default 'LIST'")
    private LayoutType layoutType;

    @Enumerated(EnumType.STRING)
    @Column(name = "link_type", columnDefinition = "varchar(10) default 'REDIRECT'")
    private LinkType linkType;

    @Column(name = "theme_color")
    private String themeColor;

    @Column(name = "logo_url")
    private String logoUrl;

    @OneToMany(mappedBy = "shortLink", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinkItemEntity> links;

    @OneToMany(mappedBy = "shortLink", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetaDataEntity> visitMetadata;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "removeMainLogo")
    private boolean removeMainLogo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public ShortLinkEntity() {
        this.links = new ArrayList<>();
        this.visitMetadata = new ArrayList<>();
    }

   // public void addVisitMetadata(MetaDataEntity metadata) {
   //        visitMetadata.add(metadata);
   //        metadata.setShortLink(this);
   //    }
   //
   //    public void removeVisitMetadata(MetaDataEntity metadata) {
   //        visitMetadata.remove(metadata);
   //        metadata.setShortLink(null);
   //    }
    public void addLink(LinkItemEntity link) {
        links.add(link);
        link.setShortLink(this);
    }

    public void removeLink(LinkItemEntity link) {
        links.remove(link);
        link.setShortLink(null);
    }


}