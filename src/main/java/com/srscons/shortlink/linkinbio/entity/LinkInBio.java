package com.srscons.shortlink.linkinbio.entity;

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
@Table(name = "link_in_bio")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class LinkInBio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title",nullable = false)
    String title;

    @Column(name = "description",nullable = false)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme_type", columnDefinition = "varchar(10) default 'AUTO'")
    ThemeType themeType;

    @Column(name = "logo_url")
    String logoUrl;

    @OneToMany(mappedBy = "linkInBio", cascade = CascadeType.ALL, orphanRemoval = true)
    List<LinkItem> links = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

}
