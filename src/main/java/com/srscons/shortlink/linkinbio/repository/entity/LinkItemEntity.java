package com.srscons.shortlink.linkinbio.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @Column(name = "logo_url")
    private String logoUrl;

    @ManyToOne
    @JoinColumn(name = "link_in_bio_id", nullable = false)
    private LinkInBioEntity linkInBio;
}