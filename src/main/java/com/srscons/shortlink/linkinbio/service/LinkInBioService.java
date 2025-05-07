package com.srscons.shortlink.linkinbio.service;


import com.srscons.shortlink.linkinbio.repository.LinkInBioRepository;
import com.srscons.shortlink.linkinbio.repository.entity.LinkInBioEntity;
import com.srscons.shortlink.linkinbio.service.dto.LinkInBioDto;
import com.srscons.shortlink.linkinbio.service.mapper.LinkInBioMapper;
import com.srscons.shortlink.linkinbio.util.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LinkInBioService {

    private final LinkInBioRepository repository;

    private final LinkInBioMapper mapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public LinkInBioDto create(LinkInBioDto linkInBioDto) {
        LinkInBioEntity entity = mapper.fromBusinessToEntity(linkInBioDto);

        MultipartFile logoFile = linkInBioDto.getLogoFile();
        if (logoFile != null && !logoFile.isEmpty()) {
            String savedFileFullPath = FileUploadUtil.saveFile(uploadDir, logoFile);
            entity.setLogoFileName(savedFileFullPath);
        }

        LinkInBioEntity savedEntity = repository.save(entity);

        return mapper.fromEntityToBusiness(savedEntity);
    }

    public LinkInBioDto findById(Long id) {
        return repository.findById(id)
                .map(mapper::fromEntityToBusiness)
                .orElse(null);
    }

}
