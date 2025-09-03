package com.fluxion.sote.diary.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fluxion.sote.global.enums.EmotionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * OCR 결과 저장 요청 DTO
 * - 기본 필드: imageUrl, content, date
 * - 선택 필드: keywordIds, emotionType
 * - FastAPI 호환을 위해 text/diaryDate 별칭 허용
 */
@Getter
@Setter
public class OcrRequest {

    private String imageUrl;

    @JsonAlias("text")          // FastAPI가 text로 보낼 때도 매핑
    private String content;

    @JsonAlias("diaryDate")     // FastAPI가 diaryDate로 보낼 때도 매핑
    private LocalDate date;

    // 선택 입력
    private List<Long> keywordIds;
    private EmotionType emotionType;
}
