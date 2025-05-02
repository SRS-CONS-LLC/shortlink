package com.srscons.shortlink.repository;

import com.srscons.shortlink.model.Smartlink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmartLinkRepository extends JpaRepository<Smartlink, Long> {
    List<Smartlink> findByDraft(boolean draft);
}