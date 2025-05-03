package com.srscons.shortlink.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class SmartlinkRequestDto {
     String title;
     String url;
     boolean draft;
     LocalDateTime createdAt;
}
