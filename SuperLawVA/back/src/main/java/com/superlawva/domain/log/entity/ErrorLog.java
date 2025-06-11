package com.superlawva.domain.log.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "error_log")
@Getter @Setter @NoArgsConstructor
public class ErrorLog {

    @Id
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String message;
    private String path;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Event event;
}