package com.srscons.shortlink.shortener.service;

import com.srscons.shortlink.common.exception.ShortLinkException;
import com.srscons.shortlink.common.exception.ShortLinkNotFoundException;
import com.srscons.shortlink.shortener.repository.ShortLinkRepository;
import com.srscons.shortlink.shortener.repository.entity.ShortLinkEntity;
import com.srscons.shortlink.shortener.repository.entity.LinkItemEntity;
import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import com.srscons.shortlink.shortener.repository.entity.enums.LinkType;
import com.srscons.shortlink.shortener.service.dto.ShortLinkDto;
import com.srscons.shortlink.shortener.service.mapper.ShortLinkMapper;
import com.srscons.shortlink.shortener.util.FileUploadService;
import io.nayuki.qrcodegen.QrCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
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
    private static final String ALPHABET = "23456789bcdfghjkmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 10;
    private static final String SVG_QR_CODE_TEMPLATE = """
                <?xml version="1.0" encoding="UTF-8"?>
                <svg xmlns="http://www.w3.org/2000/svg" version="1.1" viewBox="0 0 %d %d" shape-rendering="crispEdges">
                    <rect width="100%%" height="100%%" fill="white"/>
                    <path d="%s" stroke="black"/>
                </svg>
                """;

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
    public ShortLinkDto create(ShortLinkDto dto, String baseUrl) {
        ShortLinkEntity entity = mapper.fromBusinessToEntity(dto);
        entity.setOriginalUrl("https://www.citout.me");
        entity.setShortCode(generateUniqueShortCode());
        entity.setQrCodeSvg(generateQrCodeSvg(entity.getShortCode(), baseUrl));

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
                    } else if (itemDto.getLogoUrl() != null && !itemDto.getLogoUrl().trim().isEmpty()) {
                        item.setLogoUrl(itemDto.getLogoUrl());
                    }

                    entity.getLinks().add(item);
                    log.info("→ Added link item: {} | url={}", itemDto.getTitle(), itemDto.getUrl());
                }
            }
        }

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
        return repository.findByShortCodeIgnoreCase(shortCode)
                .map(mapper::fromEntityToBusiness)
                .orElse(null);
    }

    @Transactional
    public void saveVisitMetadata(String shortCode, HttpServletRequest request) {
        ShortLinkEntity shortLink = repository.findByShortCodeIgnoreCase(shortCode)
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
    public ShortLinkDto update(ShortLinkDto shortLinkDto, String baseUrl) {
        ShortLinkEntity foundShortLink = repository.findById(shortLinkDto.getId())
                .orElseThrow(() -> new ShortLinkNotFoundException(shortLinkDto.getId()));

        // Update main fields
        foundShortLink.setTitle(shortLinkDto.getTitle());
        foundShortLink.setOriginalUrl(shortLinkDto.getOriginalUrl());
        foundShortLink.setDescription(shortLinkDto.getDescription());
        foundShortLink.setThemeType(shortLinkDto.getThemeType());
        foundShortLink.setLayoutType(shortLinkDto.getLayoutType());
        foundShortLink.setThemeColor(shortLinkDto.getThemeColor());
        foundShortLink.setLinkType(shortLinkDto.getLinkType());

        if(!shortLinkDto.getShortCode().equalsIgnoreCase(foundShortLink.getShortCode())) {
            if(repository.existsByShortCodeIgnoreCase(shortLinkDto.getShortCode())) {
                throw new ShortLinkException("Short code already exists: " + shortLinkDto.getShortCode());
            }

            foundShortLink.setQrCodeSvg(generateQrCodeSvg(foundShortLink.getShortCode(), baseUrl));
            foundShortLink.setShortCode(shortLinkDto.getShortCode());
        }

        // Main logo logic
        if (shortLinkDto.getLogoFile() != null && !shortLinkDto.getLogoFile().isEmpty()) {
            uploadLogoIfPresent(shortLinkDto.getLogoFile(), foundShortLink);
        } else if (shortLinkDto.isRemoveMainLogo()) {
            foundShortLink.setLogoUrl(null);
        } else if (shortLinkDto.getLogoUrl() != null && !shortLinkDto.getLogoUrl().equals(foundShortLink.getLogoUrl())) {
            foundShortLink.setLogoUrl(shortLinkDto.getLogoUrl());
        }

        List<LinkItemEntity> existingLinks = foundShortLink.getLinks();
        existingLinks.clear();
        // Update link items based on index (position)
        if (shortLinkDto.getLinks() != null && shortLinkDto.getLinkType() == LinkType.BIO) {
            for (int i = 0; i < shortLinkDto.getLinks().size(); i++) {
                ShortLinkDto.LinkItemDto itemDto = shortLinkDto.getLinks().get(i);
                if (itemDto.getUrl() != null && !itemDto.getUrl().isBlank()) {
                    LinkItemEntity linkEntity = new LinkItemEntity();
                    linkEntity.setTitle(itemDto.getTitle());
                    linkEntity.setUrl(itemDto.getUrl());
                    linkEntity.setShortLink(foundShortLink);
                    linkEntity.setDeleted(false);

                    if (itemDto.getLogoFile() != null && !itemDto.getLogoFile().isEmpty()) {
                        uploadLogoIfPresent(itemDto.getLogoFile(), linkEntity);
                    } else if (itemDto.getLogoUrl() != null && !itemDto.getLogoUrl().isBlank()) {
                        linkEntity.setLogoUrl(itemDto.getLogoUrl());
                    }

                    existingLinks.add(linkEntity);
                }
            }

            foundShortLink.setLinks(existingLinks);
        }

        ShortLinkEntity updated = repository.save(foundShortLink);

        return mapper.fromEntityToBusiness(updated);
    }

    private String generateUniqueShortCode() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String shortCode = generateRandomShortCode();
            if (!repository.existsByShortCodeIgnoreCase(shortCode)) {
                return shortCode;
            }
        }
        throw new ShortLinkException("Could not generate unique short code after " + MAX_ATTEMPTS + " attempts");
    }

    private String generateRandomShortCode() {
        StringBuilder shortCode = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            shortCode.append(ALPHABET.charAt(index));
        }
        return shortCode.toString();
    }

    public String generateQrCodeSvg(String shortCode, String baseUrl) {
        String fullUrl = baseUrl +"/"+ shortCode;
        QrCode qr = QrCode.encodeText(fullUrl, QrCode.Ecc.MEDIUM);
        return toSvgString(qr);
    }

    private String toSvgString(QrCode qr) {
        int border = 1;
        int size = qr.size;

        StringBuilder pathData = new StringBuilder();
        for (int y = 0; y < size; y++) {
            boolean inLine = false;
            for (int x = 0; x < size; x++) {
                if (qr.getModule(x, y)) {
                    if (!inLine) {
                        pathData.append("M").append(x + border).append(",").append(y + border).append("h1");
                        inLine = true;
                    } else {
                        pathData.append("h1");
                    }
                } else {
                    inLine = false;
                }
            }
        }

        int fullSize = size + border * 2;

        return SVG_QR_CODE_TEMPLATE.formatted(fullSize, fullSize, pathData);
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