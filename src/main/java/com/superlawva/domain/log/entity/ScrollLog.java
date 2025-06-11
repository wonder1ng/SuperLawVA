package com.superlawva.domain.log.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scroll_log")
@Getter @Setter @NoArgsConstructor
public class ScrollLog {

    @Id
    private Long id;

    private Integer scrollPercent;       // 0–100

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Event event;
}
