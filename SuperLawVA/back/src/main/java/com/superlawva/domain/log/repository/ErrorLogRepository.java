package com.superlawva.domain.log.repository;

import com.superlawva.domain.log.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> { }