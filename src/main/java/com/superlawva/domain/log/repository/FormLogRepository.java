package com.superlawva.domain.log.repository;

import com.superlawva.domain.log.entity.FormLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormLogRepository extends JpaRepository<FormLog, Long> { }