package com.srscons.shortlink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "smartlinks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Smartlink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url;
    private boolean draft;
    private LocalDateTime createdAt;
}
