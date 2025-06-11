package com.superlawva.domain.log.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "navi_log")
@Getter @Setter @NoArgsConstructor
public class NaviLog {

    @Id
    private Long id;

    private String fromPage;
    private String toPage;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Event event;
}
