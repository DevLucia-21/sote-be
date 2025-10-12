package com.fluxion.sote.analysis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fluxion.sote.global.enums.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;

/** AI 응답을 유연하게 받는 래퍼 */
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 응답에서 제외
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponse {
    private String status;                  // "ok" | "error"
    private String message;                 // 요약/오류 메시지
    private Map<String, Object> data;       // 임의의 페이로드

    // ===== 편의 메서드 =====
    public static AnalysisResponse ok(Map<String, Object> data) {
        return new AnalysisResponse("ok", "success",
                data != null ? data : Collections.emptyMap());
    }

    public static AnalysisResponse error(String message) {
        return new AnalysisResponse("error", message, Collections.emptyMap());
    }

    public static AnalysisResponse error(String message, Map<String, Object> data) {
        return new AnalysisResponse("error", message,
                data != null ? data : Collections.emptyMap());
    }

    /** 감정 타입 변환 편의 메서드 */
    public EmotionType getEmotionType() {
        if (data != null) {
            Object label = data.get("emotionLabel");
            if (label instanceof String s) {
                return EmotionType.fromLabel(s);
            }
        }
        return null;
    }
}
