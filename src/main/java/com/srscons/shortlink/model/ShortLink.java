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

    // --- Getter və Setter ---
    private String originalUrl; // <--- bu olmalıdır!

}

