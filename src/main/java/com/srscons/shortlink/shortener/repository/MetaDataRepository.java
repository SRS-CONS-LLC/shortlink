package com.srscons.shortlink.shortener.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface MetaDataRepository extends JpaRepository<MetaDataEntity, Long> {
    Page<MetaDataEntity> findAllByShortCodeAndUserId(String shortCode, Long userId, Pageable pageable);
}