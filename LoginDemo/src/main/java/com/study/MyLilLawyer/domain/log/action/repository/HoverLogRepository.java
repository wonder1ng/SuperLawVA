package com.study.MyLilLawyer.domain.log.repository;

import com.study.MyLilLawyer.domain.log.entity.HoverLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoverLogRepository extends JpaRepository<HoverLog, Long> { }