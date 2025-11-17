package com.fluxion.sote.diary.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fluxion.sote.global.enums.EmotionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * OCR 결과 저장 요청 DTO
 * - FastAPI가 보낼 JSON 필드명(text, diaryDate 등) 별칭 허용
 * - userId를 명시적으로 받아 컨트롤러에서 User 로드
 */
@Getter
@Setter
public class OcrRequest {

    @JsonAlias({"user_id", "user"})   // FastAPI 쪽 호환 별칭
    private Long userId;              // (컨트롤러에서 User 로드용)

    private String imageUrl;

    @JsonAlias("text")                // FastAPI가 text로 보낼 때 매핑
    private String content;

    @JsonAlias("diaryDate")           // FastAPI가 diaryDate로 보낼 때 매핑
    private LocalDate date;

    // 선택 입력
    private List<Long> keywordIds;
    private EmotionType emotionType;
}