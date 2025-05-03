package com.srscons.shortlink.service.impl;

import com.srscons.shortlink.model.ShortLink;
import com.srscons.shortlink.repository.ShortLinkRepository;
import com.srscons.shortlink.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@Service
public class ShortLinkServiceImpl implements ShortLinkService {
    private final ShortLinkRepository repository;



    public String createShortLink(String originalUrl) {
        long id = repository.count() + 1;
        String shortCode = encodeBase62(id);

        ShortLink link = new ShortLink();
        link.setOriginalUrl(originalUrl);
        link.setShortCode(shortCode);
        repository.save(link);

        return "http://localhost:8080/api/" + shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        Optional<ShortLink> linkOpt = repository.findByShortCode(shortCode);
        if (linkOpt.isPresent()) {
            ShortLink link = linkOpt.get();
            link.setClickCount(link.getClickCount() + 1);
            repository.save(link);
            return link.getOriginalUrl();
        }
        return null;
    }

    public long getClickStats(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(ShortLink::getClickCount)
                .orElse(0L);
    }

    private String encodeBase62(long num) {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(chars.charAt((int) (num % 62)));
            num /= 62;
        }
        return sb.reverse().toString();
    }
}