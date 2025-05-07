package com.srscons.shortlink.linkinbio.service.impl;


import com.srscons.shortlink.linkinbio.dto.LinkInBioDto;
import com.srscons.shortlink.linkinbio.dto.LinkItemDto;
import com.srscons.shortlink.linkinbio.entity.LinkInBio;
import com.srscons.shortlink.linkinbio.exception.LinkInBioNotFoundException;
import com.srscons.shortlink.linkinbio.mapper.LinkInBioMapper;
import com.srscons.shortlink.linkinbio.repository.LinkInBioRepository;
import com.srscons.shortlink.linkinbio.service.LinkInBioService;
import com.srscons.shortlink.linkinbio.util.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class LinkInBioServiceImpl implements LinkInBioService {

    private final LinkInBioRepository repository;

    private final LinkInBioMapper mapper;


    @Value("${file.upload-dir}")
    private String uploadDir;


    @Override
    @Transactional
    public LinkInBioDto create(LinkInBioDto request) {
        if (request.getLinks() == null) {
            request.setLinks(new ArrayList<>());
        }


        while (request.getLinks().size() < 7) {
            request.getLinks().add(new LinkItemDto());
        }

        LinkInBio entity = mapper.toEntity(request);


        MultipartFile logoFile = request.getLogoFile();
        if (logoFile != null && !logoFile.isEmpty()) {
            String savedFileName = FileUploadUtil.saveFile(uploadDir, logoFile);
            entity.setLogoUrl("/uploads/" + savedFileName);
        }
        LinkInBio savedEntity = repository.save(entity);
        return mapper.toDto(savedEntity);
    }

/*
    @Override
    public LinkInBioResponse update(Long id, LinkInBioDto request) {
        LinkInBio existing = repository.findById(id)
                .orElseThrow(() -> new LinkInBioNotFoundException(id));

        LinkInBio updated = mapper.toEntity(request);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());

        return mapper.toDto(repository.save(updated));
    }

    @Override
    public LinkInBioResponse getById(Long id) {
        return null;
    }*/

}
