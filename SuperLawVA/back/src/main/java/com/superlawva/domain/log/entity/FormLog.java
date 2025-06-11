package com.superlawva.domain.log.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "form_log")
@Getter @Setter @NoArgsConstructor
public class FormLog {

    @Id
    private Long id;

    private String  formName;
    private Boolean success;
    private Integer statusCode;
    private Integer responseTime;        // ms

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Event event;
}