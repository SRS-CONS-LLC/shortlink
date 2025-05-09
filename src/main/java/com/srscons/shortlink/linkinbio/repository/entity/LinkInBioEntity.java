package com.srscons.shortlink.linkinbio.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "link_in_bio")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class LinkInBioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme_type", columnDefinition = "varchar(10) default 'AUTO'")
    private ThemeType themeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "layout_type", columnDefinition = "varchar(10) default 'LIST'")
    private LayoutType layoutType;

    @Column(name = "theme_color")
    private String themeColor;

    @Column(name = "logo_url")
    private String logoUrl;

    @OneToMany(mappedBy = "linkInBio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinkItemEntity> links;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public LinkInBioEntity() {
        this.links = new ArrayList<>();
    }

}
