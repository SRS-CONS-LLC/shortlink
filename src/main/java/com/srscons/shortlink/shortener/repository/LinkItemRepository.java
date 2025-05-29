package com.srscons.shortlink.shortener.repository;

import com.srscons.shortlink.shortener.repository.entity.LinkItemEntity;
import com.srscons.shortlink.shortener.repository.entity.ShortLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkItemRepository extends JpaRepository<LinkItemEntity, Long> {
    List<LinkItemEntity> findAllByShortLinkAndDeletedFalse(ShortLinkEntity shortLink);
}