package com.superlawva.domain.log.repository;

import com.superlawva.domain.log.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> { }