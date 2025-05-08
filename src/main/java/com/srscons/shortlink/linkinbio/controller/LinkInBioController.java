package com.srscons.shortlink.linkinbio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srscons.shortlink.linkinbio.controller.dto.request.LinkInBioRequestDto;
import com.srscons.shortlink.linkinbio.controller.dto.response.LinkInBioResponseDto;
import com.srscons.shortlink.linkinbio.controller.mapper.LinkInBioViewMapper;
import com.srscons.shortlink.linkinbio.repository.entity.LayoutType;
import com.srscons.shortlink.linkinbio.repository.entity.ThemeType;
import com.srscons.shortlink.linkinbio.service.LinkInBioService;
import com.srscons.shortlink.linkinbio.service.dto.LinkInBioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/link-in-bio")
public class LinkInBioController {

    private final LinkInBioService linkInBioService;
    private final LinkInBioViewMapper linkInBioViewMapper;

    @GetMapping
    public ResponseEntity<List<LinkInBioResponseDto>> getAllLinks() {
        List<LinkInBioDto> links = linkInBioService.findAll();
        List<LinkInBioResponseDto> responseDtos = links.stream()
            .map(linkInBioViewMapper::fromBusinessToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LinkInBioResponseDto> getLinkById(@PathVariable Long id) {
        LinkInBioDto linkInBioDto = linkInBioService.findById(id);
        if (linkInBioDto == null) {
            return ResponseEntity.notFound().build();
        }
        LinkInBioResponseDto responseDto = linkInBioViewMapper.fromBusinessToResponse(linkInBioDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<LinkInBioResponseDto> createLinkInBio(
            @ModelAttribute LinkInBioRequestDto request) {
        
        LinkInBioDto linkInBioDto = linkInBioViewMapper.fromRequestToBusiness(request);
        LinkInBioDto createdBioDto = linkInBioService.create(linkInBioDto);
        LinkInBioResponseDto responseDto = linkInBioViewMapper.fromBusinessToResponse(createdBioDto);
        
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<LinkInBioResponseDto> updateLinkInBio(
            @PathVariable Long id,
            @ModelAttribute LinkInBioRequestDto request) {
        
        // Check if link exists
        LinkInBioDto existingLink = linkInBioService.findById(id);
        if (existingLink == null) {
            return ResponseEntity.notFound().build();
        }

        LinkInBioDto linkInBioDto = linkInBioViewMapper.fromRequestToBusiness(request);
        linkInBioDto.setId(id); // Set the ID for update
        LinkInBioDto updatedBioDto = linkInBioService.update(linkInBioDto);
        LinkInBioResponseDto responseDto = linkInBioViewMapper.fromBusinessToResponse(updatedBioDto);
        
        return ResponseEntity.ok(responseDto);
    }
}
