package com.fluxion.sote.diary.entity;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.enums.EmotionType;
import com.fluxion.sote.user.entity.Keyword;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "diaries")
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 일기 작성자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 일기 날짜 */
    @Column(nullable = false)
    private LocalDate date;

    /** 일기 내용 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 작성 타입 (TEXT / OCR / STT) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WriteType writeType;

    /** 감정 타입 */
    @Enumerated(EnumType.STRING)
    private EmotionType emotionType;

    /** OCR로 작성된 경우 이미지 URL */
    private String imageUrl;

    /** 키워드 (N:M 관계) */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "diary_keywords",
            joinColumns = @JoinColumn(name = "diary_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    @Builder.Default
    private Set<Keyword> keywords = new HashSet<>();

}
