package com.srscons.shortlink.shortener.repository;

import com.srscons.shortlink.shortener.repository.entity.ShortLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortLinkRepository extends JpaRepository<ShortLinkEntity, Long> {
    Optional<ShortLinkEntity> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
    Optional<ShortLinkEntity> findByOriginalUrl(String originalUrl);
} 