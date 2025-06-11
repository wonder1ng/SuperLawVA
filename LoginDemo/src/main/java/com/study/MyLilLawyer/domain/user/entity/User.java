package com.study.MyLilLawyer.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
@Entity
@Table(indexes = {
        @Index(columnList = "kakaoId", unique = true)
})
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true)
    private Long kakaoId;

    private String email;
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
    public enum Role { USER, ADMIN }

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    @PreUpdate
    void preUpdate() { updatedAt = LocalDateTime.now(); }

    public void changeNickname(String nickname) {
        if (nickname != null) this.nickname = nickname;
    }
}
