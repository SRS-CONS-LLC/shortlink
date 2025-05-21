package com.srscons.shortlink.shortener.repository;

import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaDataRepository extends JpaRepository<MetaDataEntity, Long> {
    List<MetaDataEntity> findAllByShortLink_Id(Long shortLinkId);
} 