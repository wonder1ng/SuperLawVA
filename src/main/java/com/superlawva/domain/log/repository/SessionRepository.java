package com.superlawva.domain.log.repository;

import com.superlawva.domain.log.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
   }