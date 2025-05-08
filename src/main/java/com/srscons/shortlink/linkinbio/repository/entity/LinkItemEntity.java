package com.srscons.shortlink.linkinbio.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "link_item")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class LinkItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "logo_file_name")
    private String logoFileName;

    @ManyToOne
    @JoinColumn(name = "link_in_bio_id", nullable = false)
    private LinkInBioEntity linkInBio;
}