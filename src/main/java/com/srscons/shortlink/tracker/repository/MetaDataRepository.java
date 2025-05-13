package com.srscons.shortlink.tracker.repository;

import com.srscons.shortlink.tracker.entity.MetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaDataRepository extends JpaRepository<MetaDataEntity, Long> {

}
