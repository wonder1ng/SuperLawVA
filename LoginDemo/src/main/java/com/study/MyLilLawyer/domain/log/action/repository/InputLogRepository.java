package com.study.MyLilLawyer.domain.log.repository;

import com.study.MyLilLawyer.domain.log.entity.InputLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InputLogRepository extends JpaRepository<InputLog, Long> { }