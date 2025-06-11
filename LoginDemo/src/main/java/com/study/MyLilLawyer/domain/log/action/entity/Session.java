package com.study.MyLilLawyer.domain.log.entity;

import com.study.MyLilLawyer.domain.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity @Table(name = "sessions")
@Getter @Setter @NoArgsConstructor
public class Session {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startedAt;
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
