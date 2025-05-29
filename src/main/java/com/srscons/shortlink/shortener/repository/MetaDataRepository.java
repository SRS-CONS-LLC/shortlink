package com.srscons.shortlink.shortener.repository;

import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaDataRepository extends JpaRepository<MetaDataEntity, Long> {
    Page<MetaDataEntity> findByShortCode(String shortCode, Pageable pageable);

    @Query("SELECT m FROM MetaDataEntity m WHERE m.shortLink.shortCode = :shortCode AND m.shortLink.user.id = :userId")
    Page<MetaDataEntity> findByShortCodeAndUserId(@Param("shortCode") String shortCode, @Param("userId") Long userId, Pageable pageable);
} 