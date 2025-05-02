package com.srscons.shortlink.controller;

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
    public ResponseEntity<List<Smartlink>> getAllSmartlinks() {
        return ResponseEntity.ok(smartlinkService.getAllSmartlinks());
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<Smartlink>> getDrafts() {
        return ResponseEntity.ok(smartlinkService.getDraftSmartlinks());
    }

    @PostMapping
    public ResponseEntity<Smartlink> create(@RequestBody Smartlink smartlink) {
        return ResponseEntity.ok(smartlinkService.create(smartlink));
    }
}

