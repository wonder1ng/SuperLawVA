package com.study.MyLilLawyer.domain.log.entity;

import com.study.MyLilLawyer.domain.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

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
