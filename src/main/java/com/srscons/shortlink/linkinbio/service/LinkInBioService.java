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
    public LinkInBioDto create(LinkInBioDto dto) {
        LinkInBioEntity entity = mapper.fromBusinessToEntity(dto);

        // Upload main logo
        uploadLogoIfPresent(dto.getLogoFile(), entity);

        // Upload logos for each link item
        if (dto.getLinks() != null && entity.getLinks() != null) {
            for (int i = 0; i < dto.getLinks().size(); i++) {
                MultipartFile linkLogo = dto.getLinks().get(i).getLogoFile();
                uploadLogoIfPresent(linkLogo, entity.getLinks().get(i));
            }
        }

        LinkInBioEntity saved = repository.save(entity);
        return mapper.fromEntityToBusiness(saved);
    }

    @Transactional
    public LinkInBioDto update(LinkInBioDto dto) {
        LinkInBioEntity existing = repository.findById(dto.getId())
                .orElseThrow(() -> new LinkInBioNotFoundException(dto.getId()));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setThemeType(dto.getThemeType());
        existing.setLayoutType(dto.getLayoutType());
        existing.setThemeColor(dto.getThemeColor());

        // Update main logo
        uploadLogoIfPresent(dto.getLogoFile(), existing);

        // Remove old links and re-add new ones
        existing.getLinks().clear();

        if (dto.getLinks() != null) {
            for (LinkInBioDto.LinkItemDto itemDto : dto.getLinks()) {
                LinkItemEntity item = new LinkItemEntity();
                item.setTitle(itemDto.getTitle());
                item.setUrl(itemDto.getUrl());
                item.setLinkInBio(existing);

                uploadLogoIfPresent(itemDto.getLogoFile(), item);
                existing.getLinks().add(item);
            }
        }

        LinkInBioEntity updated = repository.save(existing);
        return mapper.fromEntityToBusiness(updated);
    }

    public LinkInBioDto findById(Long id) {
        return repository.findById(id)
                .map(mapper::fromEntityToBusiness)
                .orElseThrow(() -> new LinkInBioNotFoundException(id));
    }

    private void uploadLogoIfPresent(MultipartFile file, LinkInBioEntity entity) {
        if (file != null && !file.isEmpty()) {
            String url = fileUploadService.saveFile(file);
            entity.setLogoUrl(url);
        }
    }

    private void uploadLogoIfPresent(MultipartFile file, LinkItemEntity item) {
        if (file != null && !file.isEmpty()) {
            String url = fileUploadService.saveFile(file);
            item.setLogoUrl(url);
        }
    }
}









