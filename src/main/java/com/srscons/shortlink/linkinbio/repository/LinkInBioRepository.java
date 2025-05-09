package com.srscons.shortlink.linkinbio.repository;


import com.srscons.shortlink.linkinbio.repository.entity.LinkInBioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkInBioRepository extends JpaRepository<LinkInBioEntity, Long> {


}
