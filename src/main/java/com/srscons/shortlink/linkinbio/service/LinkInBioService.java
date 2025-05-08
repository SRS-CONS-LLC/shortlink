package com.srscons.shortlink.linkinbio.service;

import com.srscons.shortlink.linkinbio.exception.LinkInBioNotFoundException;
import com.srscons.shortlink.linkinbio.repository.LinkInBioRepository;
import com.srscons.shortlink.linkinbio.repository.entity.LinkInBioEntity;
import com.srscons.shortlink.linkinbio.repository.entity.LinkItemEntity;
import com.srscons.shortlink.linkinbio.service.dto.LinkInBioDto;
import com.srscons.shortlink.linkinbio.service.mapper.LinkInBioMapper;
import com.srscons.shortlink.linkinbio.util.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkInBioService {

    private final LinkInBioRepository repository;

    private final LinkInBioMapper mapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<LinkInBioDto> findAll() {
        return repository.findAll().stream()
            .map(mapper::fromEntityToBusiness)
            .collect(Collectors.toList());
    }

    @Transactional
    public LinkInBioDto create(LinkInBioDto linkInBioDto) {
        LinkInBioEntity entity = mapper.fromBusinessToEntity(linkInBioDto);

        // Handle main logo
        MultipartFile logoFile = linkInBioDto.getLogoFile();
        if (logoFile != null && !logoFile.isEmpty()) {
            String savedFileName = FileUploadUtil.saveFile(uploadDir, logoFile);
            entity.setLogoFileName(savedFileName);
        }

        // Handle link item logos
        if (linkInBioDto.getLinks() != null) {
            for (int i = 0; i < linkInBioDto.getLinks().size(); i++) {
                LinkInBioDto.LinkItemDto linkItemDto = linkInBioDto.getLinks().get(i);
                LinkItemEntity linkItemEntity = entity.getLinks().get(i);

                MultipartFile linkLogoFile = linkItemDto.getLogoFile();
                if (linkLogoFile != null && !linkLogoFile.isEmpty()) {
                    String savedFileName = FileUploadUtil.saveFile(uploadDir, linkLogoFile);
                    linkItemEntity.setLogoFileName(savedFileName);
                }
            }
        }

        LinkInBioEntity savedEntity = repository.save(entity);

        return mapper.fromEntityToBusiness(savedEntity);
    }

    @Transactional
    public LinkInBioDto update(LinkInBioDto linkInBioDto) {
        // Find existing entity
        LinkInBioEntity existingEntity = repository.findById(linkInBioDto.getId())
            .orElseThrow(() -> new LinkInBioNotFoundException(linkInBioDto.getId()));

        // Update basic fields
        existingEntity.setTitle(linkInBioDto.getTitle());
        existingEntity.setDescription(linkInBioDto.getDescription());
        existingEntity.setThemeType(linkInBioDto.getThemeType());
        existingEntity.setLayoutType(linkInBioDto.getLayoutType());
        existingEntity.setThemeColor(linkInBioDto.getThemeColor());

        // Handle main logo if new one is provided
        MultipartFile logoFile = linkInBioDto.getLogoFile();
        if (logoFile != null && !logoFile.isEmpty()) {
            String savedFileName = FileUploadUtil.saveFile(uploadDir, logoFile);
            existingEntity.setLogoFileName(savedFileName);
        }

        // Clear existing links
        existingEntity.getLinks().clear();

        // Add new links
        if (linkInBioDto.getLinks() != null) {
            for (LinkInBioDto.LinkItemDto linkItemDto : linkInBioDto.getLinks()) {
                LinkItemEntity linkItemEntity = new LinkItemEntity();
                linkItemEntity.setTitle(linkItemDto.getTitle());
                linkItemEntity.setUrl(linkItemDto.getUrl());
                linkItemEntity.setLinkInBio(existingEntity);

                // Handle link item logo if provided
                MultipartFile linkLogoFile = linkItemDto.getLogoFile();
                if (linkLogoFile != null && !linkLogoFile.isEmpty()) {
                    String savedFileName = FileUploadUtil.saveFile(uploadDir, linkLogoFile);
                    linkItemEntity.setLogoFileName(savedFileName);
                }

                existingEntity.getLinks().add(linkItemEntity);
            }
        }

        LinkInBioEntity updatedEntity = repository.save(existingEntity);
        return mapper.fromEntityToBusiness(updatedEntity);
    }

    public LinkInBioDto findById(Long id) {
        return repository.findById(id)
                .map(mapper::fromEntityToBusiness)
                .orElse(null);
    }

}
