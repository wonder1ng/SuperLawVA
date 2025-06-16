package com.superlawva.domain.log.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "click_log")
@Getter @Setter @NoArgsConstructor
public class ClickLog {
    @Id                      // 이벤트 PK 그대로 사용
    private Long id;

    private int  x;
    private int  y;
    private int  clickCount;
    private int  intervalAvg;
    private String element;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Event event;
}
