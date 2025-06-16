package com.superlawva.domain.log.entity;

import java.time.LocalDateTime;

import com.superlawva.domain.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "events")
@Getter @Setter @NoArgsConstructor
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;                 // click, hover …
    private String target;
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY) private Session  session;
    @ManyToOne(fetch = FetchType.LAZY) private PageView view;
    @ManyToOne(fetch = FetchType.LAZY) private User     user;
}
