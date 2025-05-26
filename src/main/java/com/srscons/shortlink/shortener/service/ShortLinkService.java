package com.srscons.shortlink.shortener.service;

import com.srscons.shortlink.shortener.exception.ShortLinkNotFoundException;
import com.srscons.shortlink.shortener.repository.ShortLinkRepository;
import com.srscons.shortlink.shortener.repository.entity.ShortLinkEntity;
import com.srscons.shortlink.shortener.repository.entity.LinkItemEntity;
import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import com.srscons.shortlink.shortener.repository.entity.enums.LinkType;
import com.srscons.shortlink.shortener.service.dto.ShortLinkDto;
import com.srscons.shortlink.shortener.service.mapper.ShortLinkMapper;
import com.srscons.shortlink.shortener.util.FileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@RequiredArgsConstructor
public class ShortLinkService {

    private final ShortLinkRepository repository;
    private final ShortLinkMapper mapper;
    private final FileUploadService fileUploadService;
    private final Random random = new SecureRandom();
    private static final Logger log = LoggerFactory.getLogger(ShortLinkService.class);
    private static final Pattern OS_VERSION_PATTERN = Pattern.compile("OS ([\\d_.]+)");
    private static final Pattern BROWSER_PATTERN = Pattern.compile("(Chrome|Firefox|Safari|Edge|Opera|MSIE|Trident)[/\\s]([\\d.]+)");
    private static final Pattern OS_PATTERN = Pattern.compile("(Windows|Mac OS X|Linux|Android|iOS)[/\\s]([\\d._]+)?");
    private static final String ALPHABET = "23456789bcdfghjkmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 10;

    public List<ShortLinkDto> findAll(Long userId) {
        try {
            List<ShortLinkEntity> entities = repository.findAllByDeletedFalseAndUserId(userId);
            log.info("Found {} non-deleted short links", entities.size());
            
            return entities.stream()
                    .map(entity -> {
                        try {
                            return mapper.fromEntityToBusiness(entity);
                        } catch (Exception e) {
                            log.error("Error mapping entity to DTO: {}", e.getMessage(), e);
                            return null;
                        }
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error in findAll: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public ShortLinkDto create(ShortLinkDto dto) {
        ShortLinkEntity entity = mapper.fromBusinessToEntity(dto);
        entity.setOriginalUrl("https://www.citout.me");
        entity.setShortCode(generateUniqueShortCode());

        // Handle link items
        if (dto.getLinks() != null) {
            List<ShortLinkDto.LinkItemDto> incomingLinks = dto.getLinks();
            log.info("📦 Creating new Smartlink with {} link items", incomingLinks.size());

            for (ShortLinkDto.LinkItemDto itemDto : incomingLinks) {
                if (itemDto.getUrl() != null && !itemDto.getUrl().trim().isEmpty()) {
                    LinkItemEntity item = new LinkItemEntity();
                    item.setTitle(itemDto.getTitle());
                    item.setUrl(itemDto.getUrl());
                    item.setShortLink(entity);
                    item.setDeleted(false);

                    if (itemDto.getLogoFile() != null && !itemDto.getLogoFile().isEmpty()) {
                        uploadLogoIfPresent(itemDto.getLogoFile(), item);
                    }
                    // Always set logoUrl if present (for default icons)
                    if (itemDto.getLogoUrl() != null && !itemDto.getLogoUrl().trim().isEmpty()) {
                        item.setLogoUrl(itemDto.getLogoUrl());
                    }

                    entity.getLinks().add(item);
                    log.info("→ Added link item: {} | url={}", itemDto.getTitle(), itemDto.getUrl());
                }
            }
        }

        System.out.println("Saving ShortLink with linkType: " + entity.getLinkType());
        System.out.println("Existing logo: " + entity.getLogoUrl());
        System.out.println("DTO logo: " + dto.getLogoUrl());
        ShortLinkEntity saved = repository.save(entity);
        return mapper.fromEntityToBusiness(saved);
    }

    public ShortLinkDto findById(Long id) {
        ShortLinkEntity entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ShortLinkNotFoundException(id));

        return mapper.fromEntityToBusiness(entity);
    }

    @Transactional
    public ShortLinkDto getShortLinkByCode(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(mapper::fromEntityToBusiness)
                .orElse(null);
    }

    @Transactional
    public void saveVisitMetadata(String shortCode, HttpServletRequest request) {
        ShortLinkEntity shortLink = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortLinkNotFoundException(shortCode));

        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        MetaDataEntity metadata = new MetaDataEntity();
        metadata.setShortLink(shortLink);
        metadata.setClickTime(LocalDateTime.now());
        metadata.setIpAddress(getClientIp(request));
        metadata.setUserAgent(userAgent);
        metadata.setReferer(referer);
        metadata.setChannel(detectChannel(referer));
        metadata.setSmartlinkType(detectSmartlinkType(shortLink.getOriginalUrl()));
        metadata.setProxy(detectProxy(request));
        metadata.setVpn(detectVPN(request));

        shortLink.addVisitMetadata(metadata);
        repository.save(shortLink);
    }


    @Transactional
    public ShortLinkDto update(ShortLinkDto dto) {

        ShortLinkEntity existing = repository.findById(dto.getId())
                .orElseThrow(() -> new ShortLinkNotFoundException(dto.getId()));

        // Update main fields
        existing.setTitle(dto.getTitle());
        existing.setOriginalUrl(dto.getOriginalUrl());
        existing.setDescription(dto.getDescription());
        existing.setThemeType(dto.getThemeType());
        existing.setLayoutType(dto.getLayoutType());
        existing.setThemeColor(dto.getThemeColor());
        existing.setLinkType(dto.getLinkType());

        log.info(" update() method was called for Smartlink ID: {}", dto.getId());

        // Main logo logic
        if (dto.getLogoFile() != null && !dto.getLogoFile().isEmpty()) {
            uploadLogoIfPresent(dto.getLogoFile(), existing);
        } else if (dto.isRemoveMainLogo()) {
            existing.setLogoUrl(null);
        } else if (dto.getLogoUrl() != null && !dto.getLogoUrl().equals(existing.getLogoUrl())) {
            existing.setLogoUrl(dto.getLogoUrl());
        }

        // Update link items based on index (position)
        if (dto.getLinks() != null && dto.getLinkType() == LinkType.BIO) {
            List<ShortLinkDto.LinkItemDto> incomingLinks = dto.getLinks();
            log.info("📦 Incoming links:");
            incomingLinks.forEach(l -> log.info("→ {} | deleted = {}", l.getTitle(), l.getDeleted()));

            List<LinkItemEntity> existingLinks = existing.getLinks();
            log.info("📦 Existing links count: {}", existingLinks.size());
            existingLinks.forEach(l -> log.info("→ {} | deleted = {}", l.getTitle(), l.isDeleted()));
            
            // Build a map of incoming links by id (or by title+url if no id)
            Map<Long, ShortLinkDto.LinkItemDto> incomingById = incomingLinks.stream()
                .filter(l -> l.getId() != null)
                .collect(Collectors.toMap(ShortLinkDto.LinkItemDto::getId, l -> l));

            // Mark as deleted any existing link not present in the incoming list
            for (LinkItemEntity item : existingLinks) {
                if (!incomingById.containsKey(item.getId())) {
                    item.setDeleted(true);
                    log.info("🗑️ Marked link as deleted (not present in incoming): {}", item.getId());
                }
            }

            // Update or add links
            for (ShortLinkDto.LinkItemDto itemDto : incomingLinks) {
                if (itemDto.getId() != null) {
                    // Update existing
                    LinkItemEntity item = existingLinks.stream()
                        .filter(e -> e.getId() != null && e.getId().equals(itemDto.getId()))
                        .findFirst().orElse(null);
                    if (item != null) {
                        item.setTitle(itemDto.getTitle());
                        item.setUrl(itemDto.getUrl());
                        item.setDeleted(itemDto.getDeleted() != null && itemDto.getDeleted());
                        item.setLogoUrl(itemDto.getLogoUrl());
                    }
                } else {
                    // Add new link
                    LinkItemEntity newItem = new LinkItemEntity();
                    newItem.setTitle(itemDto.getTitle());
                    newItem.setUrl(itemDto.getUrl());
                    newItem.setShortLink(existing);
                    newItem.setDeleted(itemDto.getDeleted() != null && itemDto.getDeleted());

                    if (itemDto.getLogoFile() != null && !itemDto.getLogoFile().isEmpty()) {
                        uploadLogoIfPresent(itemDto.getLogoFile(), newItem);
                    }
                    // Always set logoUrl if present (for default icons)
                    if (itemDto.getLogoUrl() != null && !itemDto.getLogoUrl().trim().isEmpty()) {
                        newItem.setLogoUrl(itemDto.getLogoUrl());
                    }

                    existing.getLinks().add(newItem);
                    log.info("➕ Added new link: {} | url={}", itemDto.getTitle(), itemDto.getUrl());
                }
            }
        }

        ShortLinkEntity updated = repository.save(existing);
        return mapper.fromEntityToBusiness(updated);
    }

    private String generateUniqueShortCode() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String shortCode = generateRandomShortCode();
            if (!repository.existsByShortCode(shortCode)) {
                return shortCode;
            }
        }
        throw new IllegalStateException("Could not generate unique short code after " + MAX_ATTEMPTS + " attempts");
    }

    private String generateRandomShortCode() {
        StringBuilder shortCode = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            shortCode.append(ALPHABET.charAt(index));
        }
        return shortCode.toString();
    }

    private void uploadLogoIfPresent(MultipartFile logoFile, ShortLinkEntity entity) {
        if (logoFile != null && !logoFile.isEmpty()) {
            String logoUrl = fileUploadService.saveFile(logoFile);
            entity.setLogoUrl(logoUrl);
        }
    }

    private void uploadLogoIfPresent(MultipartFile logoFile, LinkItemEntity entity) {
        if (logoFile != null && !logoFile.isEmpty()) {
            String logoUrl = fileUploadService.saveFile(logoFile);
            entity.setLogoUrl(logoUrl);
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String value = idx < pair.length() - 1 ? pair.substring(idx + 1) : "";
                params.put(key, value);
            }
        }
        return params;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String detectChannel(String url) {
        if (url == null) return null;

        if (url.contains("facebook.com") || url.contains("fb.com")) return "Facebook";
        if (url.contains("twitter.com") || url.contains("x.com")) return "Twitter";
        if (url.contains("instagram.com")) return "Instagram";
        if (url.contains("linkedin.com")) return "LinkedIn";
        if (url.contains("youtube.com")) return "YouTube";
        if (url.contains("tiktok.com")) return "TikTok";
        if (url.contains("pinterest.com")) return "Pinterest";
        if (url.contains("reddit.com")) return "Reddit";
        if (url.contains("whatsapp.com")) return "WhatsApp";
        if (url.contains("telegram.me")) return "Telegram";

        return "Direct";
    }

    private String detectSmartlinkType(String url) {
        if (url == null) return null;

        if (url.contains("amazon.com") || url.contains("amzn.to")) return "Amazon";
        if (url.contains("ebay.com")) return "eBay";
        if (url.contains("etsy.com")) return "Etsy";
        if (url.contains("shopify.com")) return "Shopify";
        if (url.contains("aliexpress.com")) return "AliExpress";
        if (url.contains("walmart.com")) return "Walmart";
        if (url.contains("target.com")) return "Target";
        if (url.contains("bestbuy.com")) return "Best Buy";

        return "Standard";
    }

    private String detectProxy(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");
        String via = request.getHeader("Via");

        if (forwardedFor != null || realIp != null || via != null) {
            return "Detected";
        }
        return null;
    }

    private String detectVPN(HttpServletRequest request) {
        // This is a simplified check. In a real application, you might want to use a more sophisticated
        // VPN detection service or database
        String ip = getClientIp(request);
        String hostname = request.getRemoteHost();

        // Check for common VPN hostname patterns
        if (hostname != null && (
                hostname.contains("vpn") ||
                        hostname.contains("proxy") ||
                        hostname.contains("tor") ||
                        hostname.contains("anonymous")
        )) {
            return "Detected";
        }

        return null;
    }

    @Transactional
    public void softDeleteShortlink(Long shortlinkId) {
        ShortLinkEntity shortlink = repository.findById(shortlinkId)
                .orElseThrow(() -> new ShortLinkNotFoundException("Shortlink not found"));

        shortlink.setDeleted(true);

        for (LinkItemEntity item : shortlink.getLinks()) {
            item.setDeleted(true);
        }

        repository.save(shortlink);
    }

} 