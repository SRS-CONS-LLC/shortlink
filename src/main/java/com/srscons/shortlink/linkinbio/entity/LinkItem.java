package com.srscons.shortlink.linkinbio.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "link_item")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class LinkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title")
    String title;

    @Column(name = "url", nullable = false)
    String url;

    @ManyToOne
    @JoinColumn(name = "link_in_bio_id", nullable = false)
    LinkInBio linkInBio;
}