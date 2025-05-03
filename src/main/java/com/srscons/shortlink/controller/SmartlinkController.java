package com.srscons.shortlink.controller;

import com.srscons.shortlink.dto.SmartlinkRequestDto;
import com.srscons.shortlink.dto.SmartlinkResponseDto;
import com.srscons.shortlink.model.Smartlink;
import com.srscons.shortlink.service.SmartlinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/smartlinks")
@RequiredArgsConstructor
public class SmartlinkController {
    private final SmartlinkService smartlinkService;

    @GetMapping
    public ResponseEntity<List<SmartlinkResponseDto>> getAllSmartlinks() {
        return ResponseEntity.ok(smartlinkService.getAllSmartlinks());
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<SmartlinkResponseDto>> getDrafts() {
        return ResponseEntity.ok(smartlinkService.getDraftSmartlinks());
    }

    @PostMapping
    public ResponseEntity<SmartlinkResponseDto> create(@RequestBody SmartlinkRequestDto smartlinkDto) {
        return ResponseEntity.ok(smartlinkService.create(smartlinkDto));
    }
}

