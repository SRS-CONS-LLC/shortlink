package com.srscons.shortlink.VisitLog.Repository;

import com.srscons.shortlink.VisitLog.Entity.MetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaDataRepository extends JpaRepository<MetaDataEntity, Long> {

}
