package com.srscons.shortlink.tracker.repository;

import com.srscons.shortlink.tracker.entity.ShortlinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortLinkRepository extends JpaRepository<ShortlinkEntity, Long> {
    Optional<ShortlinkEntity> findByShortCode(String shortCode);

}
