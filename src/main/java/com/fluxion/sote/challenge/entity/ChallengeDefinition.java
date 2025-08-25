package com.fluxion.sote.challenge.entity;

import com.fluxion.sote.global.enums.EmotionType;
import com.fluxion.sote.global.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChallengeDefinition extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // 챌린지 내용

    @Enumerated(EnumType.STRING)
    private EmotionType emotionType; // 감정 유형 (JOY, SADNESS 등)

    private String category; // 활동 카테고리 (ex. 루틴, 운동, 명상 등)

    private boolean isDeleted;

    public void update(String content, EmotionType emotionType, String category) {
        this.content = content;
        this.emotionType = emotionType;
        this.category = category;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

}
