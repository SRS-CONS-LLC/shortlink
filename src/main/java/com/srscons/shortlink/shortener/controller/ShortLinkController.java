package com.srscons.shortlink.shortener.controller;

import com.srscons.shortlink.auth.entity.UserEntity;
import com.srscons.shortlink.auth.util.CONSTANTS;
import com.srscons.shortlink.shortener.controller.dto.request.ShortLinkRequestDto;
import com.srscons.shortlink.shortener.controller.dto.response.ShortLinkResponseDto;
import com.srscons.shortlink.shortener.controller.mapper.ShortLinkViewMapper;
import com.srscons.shortlink.common.exception.ShortLinkNotFoundException;
import com.srscons.shortlink.shortener.service.ShortLinkService;
import com.srscons.shortlink.shortener.service.dto.ShortLinkDto;
import com.srscons.shortlink.shortener.service.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
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
            @Valid @ModelAttribute ShortLinkRequestDto shortLinkRequestDto,
            HttpServletRequest request) {
        ShortLinkDto shortLinkDto = shortLinkViewMapper.fromRequestToBusiness(shortLinkRequestDto);

        UserEntity loggedInUser = (UserEntity) request.getAttribute(CONSTANTS.LOGGED_IN_USER);
        shortLinkDto.setUser(new UserDto(loggedInUser.getId()));

        ShortLinkDto createdDto = shortLinkService.create(shortLinkDto, getBaseUrl(request));

        ShortLinkResponseDto responseDto = shortLinkViewMapper.fromBusinessToResponse(createdDto);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShortLinkResponseDto> updateShortLink(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute ShortLinkRequestDto shortLinkRequestDto,
            HttpServletRequest request) {
        try {
            ShortLinkDto shortLinkDto = shortLinkViewMapper.fromRequestToBusiness(shortLinkRequestDto);
            shortLinkDto.setId(id);
            ShortLinkDto updatedDto = shortLinkService.update(shortLinkDto, getBaseUrl(request));
            ShortLinkResponseDto responseDto = shortLinkViewMapper.fromBusinessToResponse(updatedDto);
            return ResponseEntity.ok(responseDto);
        } catch (ShortLinkNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ShortLinkResponseDto>> getAllLinks(HttpServletRequest request) {
        try {
            UserEntity loggedInUser = (UserEntity) request.getAttribute(CONSTANTS.LOGGED_IN_USER);
            List<ShortLinkDto> links = shortLinkService.findAll(loggedInUser.getId());
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

    private static String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();             // http or https
        String serverName = request.getServerName();     // domain or IP
        int serverPort = request.getServerPort();        // 80, 443, etc.
        String contextPath = request.getContextPath();   // usually ""

        // Build base URL
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        // Only add port if it's not default
        if ((scheme.equals("http") && serverPort != 80) ||
                (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath);

        return url.toString(); // e.g., https://example.com or http://localhost:8080/app
    }

} 