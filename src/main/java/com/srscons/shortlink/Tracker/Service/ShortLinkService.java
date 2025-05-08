package com.srscons.shortlink.Tracker.Service;

import com.srscons.shortlink.Tracker.Entity.ShortlinkEntity;
import com.srscons.shortlink.Tracker.Repository.ShortLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShortLinkService {
    private final ShortLinkRepository shortLinkRepository;

    public String createShortLink(String originalUrl){
        String shortCode = generationCode();
        ShortlinkEntity shortlinkEntity = ShortlinkEntity.builder()
                .shortCode(shortCode)
                .originalUrl(originalUrl)
                .build();
        shortLinkRepository.save(shortlinkEntity);
        return shortCode;
    }
    public String getOriginalUrl(String shortCode){
        return shortLinkRepository.findByShortCode(shortCode)
                .map(ShortlinkEntity::getOriginalUrl)
                .orElse(null);
    }
    private String generationCode(){
        return UUID.randomUUID().toString().substring(0,6);
    }
}
