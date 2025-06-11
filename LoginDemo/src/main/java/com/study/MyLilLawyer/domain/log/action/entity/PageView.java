package com.study.MyLilLawyer.domain.log.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity @Table(name = "page_views")
@Getter @Setter @NoArgsConstructor
public class PageView {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;
    private LocalDateTime startedAt;
    private Integer duration;            // ms

    @ManyToOne(fetch = FetchType.LAZY)
    private Session session;
}
