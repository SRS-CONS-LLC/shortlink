package com.srscons.shortlink.shortener.service;

import com.srscons.shortlink.shortener.exception.ShortLinkNotFoundException;
import com.srscons.shortlink.shortener.repository.ShortLinkRepository;
import com.srscons.shortlink.shortener.repository.entity.ShortLinkEntity;
import com.srscons.shortlink.shortener.repository.entity.LinkItemEntity;
import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
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

@Service
@RequiredArgsConstructor
public class ShortLinkService {

    private final ShortLinkRepository repository;
    private final ShortLinkMapper mapper;
    private final FileUploadService fileUploadService;
    private final Random random = new SecureRandom();

    private static final Pattern OS_VERSION_PATTERN = Pattern.compile("OS ([\\d_.]+)");
    private static final Pattern BROWSER_PATTERN = Pattern.compile("(Chrome|Firefox|Safari|Edge|Opera|MSIE|Trident)[/\\s]([\\d.]+)");
    private static final Pattern OS_PATTERN = Pattern.compile("(Windows|Mac OS X|Linux|Android|iOS)[/\\s]([\\d._]+)?");
    private static final String ALPHABET = "23456789bcdfghjkmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 10;

    public List<ShortLinkDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::fromEntityToBusiness)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShortLinkDto create(ShortLinkDto dto) {
        ShortLinkEntity entity = mapper.fromBusinessToEntity(dto);
        entity.setOriginalUrl("https://www.ctout.com");
        entity.setShortCode(generateUniqueShortCode());

        ShortLinkEntity saved = repository.save(entity);
        return mapper.fromEntityToBusiness(saved);
    }

    public ShortLinkDto findById(Long id) {
        return repository.findById(id)
                .map(mapper::fromEntityToBusiness)
                .orElseThrow(() -> new ShortLinkNotFoundException(id));
    }

    @Transactional
    public String getOriginalUrl(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(ShortLinkEntity::getOriginalUrl)
                .orElse(null);
    }

    @Transactional
    public void saveVisitMetadata(String shortCode, HttpServletRequest request) {
        ShortLinkEntity shortLink = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortLinkNotFoundException(shortCode));

        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        String queryString = request.getQueryString();

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

        // Parse user agent manually
        if (userAgent != null) {
            // Detect browser
            java.util.regex.Matcher browserMatcher = BROWSER_PATTERN.matcher(userAgent);
            if (browserMatcher.find()) {
                metadata.setBrowser(browserMatcher.group(1));
            }

            // Detect OS
            java.util.regex.Matcher osMatcher = OS_PATTERN.matcher(userAgent);
            if (osMatcher.find()) {
                String os = osMatcher.group(1);
                metadata.setOs(os);
                metadata.setOsVersion(osMatcher.group(2));
            }

            // Detect device type
            String userAgentLower = userAgent.toLowerCase();
            metadata.setIsMobile(userAgentLower.contains("mobile") || userAgentLower.contains("android") || userAgentLower.contains("iphone"));
            metadata.setIsTablet(userAgentLower.contains("tablet") || userAgentLower.contains("ipad"));
            metadata.setIsDesktop(!metadata.getIsMobile() && !metadata.getIsTablet());
        }

        // Parse UTM parameters
        if (queryString != null) {
            Map<String, String> params = parseQueryString(queryString);
            metadata.setUtmSource(params.get("utm_source"));
            metadata.setUtmMedium(params.get("utm_medium"));
            metadata.setUtmCampaign(params.get("utm_campaign"));
            metadata.setUtmTerm(params.get("utm_term"));
            metadata.setUtmContent(params.get("utm_content"));
        }

        shortLink.getVisitMetadata().add(metadata);
        repository.save(shortLink);
    }

    @Transactional
    public ShortLinkDto update(ShortLinkDto dto) {
        ShortLinkEntity existing = repository.findById(dto.getId())
                .orElseThrow(() -> new ShortLinkNotFoundException(dto.getId()));

        existing.setTitle(dto.getTitle());
        existing.setOriginalUrl(dto.getOriginalUrl());
        existing.setDescription(dto.getDescription());
        existing.setThemeType(dto.getThemeType());
        existing.setLayoutType(dto.getLayoutType());
        existing.setThemeColor(dto.getThemeColor());

        // Handle main logo
        if (dto.getLogoFile() != null && !dto.getLogoFile().isEmpty()) {
            // New logo file uploaded
            uploadLogoIfPresent(dto.getLogoFile(), existing);
        } else if (dto.getLogoUrl() != null) {
            // Preserve existing logo URL
            existing.setLogoUrl(dto.getLogoUrl());
        } else {
            // Remove logo
            existing.setLogoUrl(null);
        }

        // Remove old links and re-add new ones
        existing.getLinks().clear();

        if (dto.getLinks() != null) {
            for (ShortLinkDto.LinkItemDto itemDto : dto.getLinks()) {
                LinkItemEntity item = new LinkItemEntity();
                item.setTitle(itemDto.getTitle());
                item.setUrl(itemDto.getUrl());
                item.setShortLink(existing);

                // Handle link logo
                if (itemDto.getLogoFile() != null && !itemDto.getLogoFile().isEmpty()) {
                    // New logo file uploaded
                    uploadLogoIfPresent(itemDto.getLogoFile(), item);
                } else if (itemDto.getLogoUrl() != null) {
                    // Preserve existing logo URL
                    item.setLogoUrl(itemDto.getLogoUrl());
                } else {
                    // Remove logo
                    item.setLogoUrl(null);
                }

                existing.getLinks().add(item);
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

} 