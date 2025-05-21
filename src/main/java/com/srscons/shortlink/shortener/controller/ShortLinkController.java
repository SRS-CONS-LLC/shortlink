package com.srscons.shortlink.shortener.controller;

import com.srscons.shortlink.shortener.controller.dto.request.ShortLinkRequestDto;
import com.srscons.shortlink.shortener.controller.dto.response.ShortLinkResponseDto;
import com.srscons.shortlink.shortener.controller.mapper.ShortLinkViewMapper;
import com.srscons.shortlink.shortener.exception.ShortLinkNotFoundException;
import com.srscons.shortlink.shortener.service.ShortLinkService;
import com.srscons.shortlink.shortener.service.dto.ShortLinkDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-links")
@RequiredArgsConstructor
public class ShortLinkController {
    private static final Logger log = LoggerFactory.getLogger(ShortLinkController.class);
    
    private final ShortLinkService shortLinkService;
    private final ShortLinkViewMapper shortLinkViewMapper;

    @PostMapping
    public ResponseEntity<ShortLinkResponseDto> createShortLink(
            @Valid @ModelAttribute ShortLinkRequestDto request) {
        ShortLinkDto shortLinkDto = shortLinkViewMapper.fromRequestToBusiness(request);
        ShortLinkDto createdDto = shortLinkService.create(shortLinkDto);
        ShortLinkResponseDto responseDto = shortLinkViewMapper.fromBusinessToResponse(createdDto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShortLinkResponseDto> updateShortLink(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute ShortLinkRequestDto request) {
        try {
            ShortLinkDto shortLinkDto = shortLinkViewMapper.fromRequestToBusiness(request);
            shortLinkDto.setId(id);
            ShortLinkDto updatedDto = shortLinkService.update(shortLinkDto);
            ShortLinkResponseDto responseDto = shortLinkViewMapper.fromBusinessToResponse(updatedDto);
            return ResponseEntity.ok(responseDto);
        } catch (ShortLinkNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ShortLinkResponseDto>> getAllLinks() {
        try {
            List<ShortLinkDto> links = shortLinkService.findAll();
            List<ShortLinkResponseDto> responseDtos = links.stream()
                    .map(shortLinkViewMapper::fromBusinessToResponse)
                    .toList();
            return ResponseEntity.ok(responseDtos);
        } catch (Exception e) {
            log.error("Error getting all links: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShortLinkResponseDto> getLinkById(@PathVariable("id") Long id) {
        try {
            ShortLinkDto shortLinkDto = shortLinkService.findById(id);
            ShortLinkResponseDto responseDto = shortLinkViewMapper.fromBusinessToResponse(shortLinkDto);
            return ResponseEntity.ok(responseDto);
        } catch (ShortLinkNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteShortLink(@PathVariable("id") Long id) {
        try {
            shortLinkService.softDeleteShortlink(id);
            return ResponseEntity.noContent().build();
        } catch (ShortLinkNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

} 