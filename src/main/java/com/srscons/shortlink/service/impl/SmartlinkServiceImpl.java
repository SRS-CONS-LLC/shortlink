package com.srscons.shortlink.service.impl;

import com.srscons.shortlink.model.Smartlink;
import com.srscons.shortlink.repository.SmartLinkRepository;
import com.srscons.shortlink.service.SmartlinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SmartlinkServiceImpl implements SmartlinkService {
    private final SmartLinkRepository smartlinkRepository;

    @Override
    public List<Smartlink> getAllSmartlinks() {
        return smartlinkRepository.findAll();
    }

    @Override
    public List<Smartlink> getDraftSmartlinks() {
        return smartlinkRepository.findByDraft(true);
    }

    @Override
    public Smartlink create(Smartlink smartlink) {
        smartlink.setCreatedAt(LocalDateTime.now());
        return smartlinkRepository.save(smartlink);
    }
}




