package com.srscons.shortlink.VisitLog;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "visit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortCode;
    private String ipAddress;
    private String userAgent;
    private String referer;
    private String acceptLanguage;
    private LocalDateTime visitedAt;
}
