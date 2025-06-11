package com.study.MyLilLawyer.domain.log.repository;

import com.study.MyLilLawyer.domain.log.entity.ScrollLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrollLogRepository extends JpaRepository<ScrollLog, Long> { }