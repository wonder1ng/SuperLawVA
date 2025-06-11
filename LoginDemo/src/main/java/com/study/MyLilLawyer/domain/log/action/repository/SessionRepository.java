package com.study.MyLilLawyer.domain.log.repository;

import com.study.MyLilLawyer.domain.log.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
   }