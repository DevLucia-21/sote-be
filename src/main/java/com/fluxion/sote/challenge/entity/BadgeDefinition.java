package com.fluxion.sote.challenge.entity;

import com.fluxion.sote.global.enums.EmotionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BadgeDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 뱃지 이름 (예: "슬픔 정복자 I")

    @Column(length = 255)
    private String description; // 뱃지 설명

    @Enumerated(EnumType.STRING)
    private EmotionType emotionType; // 감정 기반 뱃지 (NULL이면 공용/카테고리 뱃지)

    @Column(length = 50)
    private String category; // 카테고리 기반 뱃지 (NULL이면 감정/공용 뱃지)

    @Column(nullable = false)
    private int conditionCount; // 몇 번 완료해야 뱃지 지급되는지
}
