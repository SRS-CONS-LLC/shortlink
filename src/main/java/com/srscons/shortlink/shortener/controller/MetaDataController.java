package com.srscons.shortlink.shortener.controller;

import com.srscons.shortlink.auth.entity.UserEntity;
import com.srscons.shortlink.auth.util.CONSTANTS;
import com.srscons.shortlink.shortener.service.MetaDataService;
import com.srscons.shortlink.shortener.service.dto.MetaDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/metadata")
@RequiredArgsConstructor
public class MetaDataController {
    private final MetaDataService metaDataService;

    @GetMapping
    public ResponseEntity<Page<MetaDataDto>> getAllMetaData(Pageable pageable) {
        return ResponseEntity.ok(metaDataService.getAllMetaData(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaDataDto> getMetaDataById(@PathVariable Long id) {
        MetaDataDto metadata = metaDataService.getMetaDataById(id);
        if (metadata == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/shortcode/{shortCode}")
    public ResponseEntity<Page<MetaDataDto>> getMetaDataByShortCode(
            @PathVariable String shortCode,
            Pageable pageable,
            HttpServletRequest request) {
        UserEntity loggedInUser = (UserEntity) request.getAttribute(CONSTANTS.LOGGED_IN_USER);
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(metaDataService.getMetaDataByShortCodeAndUserId(shortCode, loggedInUser.getId(), pageable));
    }
} 