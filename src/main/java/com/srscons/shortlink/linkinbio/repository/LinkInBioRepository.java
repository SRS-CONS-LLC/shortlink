package com.srscons.shortlink.linkinbio.repository;


import com.srscons.shortlink.linkinbio.entity.LinkInBio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkInBioRepository extends JpaRepository<LinkInBio, Long> {

     Optional<LinkInBio> findByTitle(String title);
}
