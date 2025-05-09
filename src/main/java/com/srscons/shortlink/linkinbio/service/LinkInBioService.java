package com.srscons.shortlink.linkinbio.service;
import com.srscons.shortlink.linkinbio.exception.LinkInBioNotFoundException;
import com.srscons.shortlink.linkinbio.repository.LinkInBioRepository;
import com.srscons.shortlink.linkinbio.repository.entity.LinkInBioEntity;
import com.srscons.shortlink.linkinbio.repository.entity.LinkItemEntity;
import com.srscons.shortlink.linkinbio.service.dto.LinkInBioDto;
import com.srscons.shortlink.linkinbio.service.mapper.LinkInBioMapper;
import com.srscons.shortlink.linkinbio.util.FileUploadService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkInBioService {

    private final LinkInBioRepository repository;
    private final LinkInBioMapper mapper;
    private final FileUploadService fileUploadService;

    public List<LinkInBioDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::fromEntityToBusiness)
                .collect(Collectors.toList());
    }

    @Transactional
    public LinkInBioDto create(LinkInBioDto linkInBioDto) {
        LinkInBioEntity entity = mapper.fromBusinessToEntity(linkInBioDto);

        MultipartFile logoFile = linkInBioDto.getLogoFile();
        if (logoFile != null && !logoFile.isEmpty()) {
            String cdnUrl = fileUploadService.saveFile(logoFile);
            entity.setLogoUrl(cdnUrl);
        }

        if (linkInBioDto.getLinks() != null) {
            for (int i = 0; i < linkInBioDto.getLinks().size(); i++) {
                LinkInBioDto.LinkItemDto linkItemDto = linkInBioDto.getLinks().get(i);
                LinkItemEntity linkItemEntity = entity.getLinks().get(i);

                MultipartFile linkLogoFile = linkItemDto.getLogoFile();
                if (linkLogoFile != null && !linkLogoFile.isEmpty()) {
                    String cdnUrl = fileUploadService.saveFile(linkLogoFile);
                    linkItemEntity.setLogoUrl(cdnUrl);
                }
            }
        }

        LinkInBioEntity savedEntity = repository.save(entity);
        return mapper.fromEntityToBusiness(savedEntity);
    }

    @Transactional
    public LinkInBioDto update(LinkInBioDto linkInBioDto) {
        LinkInBioEntity existingEntity = repository.findById(linkInBioDto.getId())
                .orElseThrow(() -> new LinkInBioNotFoundException(linkInBioDto.getId()));

        existingEntity.setTitle(linkInBioDto.getTitle());
        existingEntity.setDescription(linkInBioDto.getDescription());
        existingEntity.setThemeType(linkInBioDto.getThemeType());
        existingEntity.setLayoutType(linkInBioDto.getLayoutType());
        existingEntity.setThemeColor(linkInBioDto.getThemeColor());

        MultipartFile logoFile = linkInBioDto.getLogoFile();
        if (logoFile != null && !logoFile.isEmpty()) {
            String cdnUrl = fileUploadService.saveFile(logoFile);
            existingEntity.setLogoUrl(cdnUrl);
        }

        existingEntity.getLinks().clear();

        if (linkInBioDto.getLinks() != null) {
            for (LinkInBioDto.LinkItemDto linkItemDto : linkInBioDto.getLinks()) {
                LinkItemEntity linkItemEntity = new LinkItemEntity();
                linkItemEntity.setTitle(linkItemDto.getTitle());
                linkItemEntity.setUrl(linkItemDto.getUrl());
                linkItemEntity.setLinkInBio(existingEntity);

                MultipartFile linkLogoFile = linkItemDto.getLogoFile();
                if (linkLogoFile != null && !linkLogoFile.isEmpty()) {
                    String cdnUrl = fileUploadService.saveFile(linkLogoFile);
                    linkItemEntity.setLogoUrl(cdnUrl);
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
