package com.srscons.shortlink.repository;

import com.srscons.shortlink.model.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {
    ShortLink findByCode(String code);
}
