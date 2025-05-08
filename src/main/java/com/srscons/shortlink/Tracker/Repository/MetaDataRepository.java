package com.srscons.shortlink.Tracker.Repository;

import com.srscons.shortlink.Tracker.Entity.MetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaDataRepository extends JpaRepository<MetaDataEntity, Long> {

}
