package com.srscons.shortlink.linkinbio.repository;

import com.srscons.shortlink.linkinbio.repository.entity.MetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaDataRepository extends JpaRepository<MetaDataEntity, Long> {
} 