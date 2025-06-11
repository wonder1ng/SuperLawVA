package com.study.MyLilLawyer.domain.log.repository;

import com.study.MyLilLawyer.domain.log.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> { }