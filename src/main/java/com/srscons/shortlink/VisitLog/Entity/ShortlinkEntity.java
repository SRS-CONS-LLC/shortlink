package com.srscons.shortlink.VisitLog.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "short_links")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortlinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private  String shortCode;
    @Column(nullable = false)
    private String originalUrl;

}
