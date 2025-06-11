package com.superlawva.domain.log.repository;

import com.superlawva.domain.log.entity.PageView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageViewRepository extends JpaRepository<PageView, Long> { }