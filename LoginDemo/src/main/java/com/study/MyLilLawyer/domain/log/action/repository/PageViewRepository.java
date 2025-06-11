package com.study.MyLilLawyer.domain.log.repository;

import com.study.MyLilLawyer.domain.log.entity.PageView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageViewRepository extends JpaRepository<PageView, Long> { }