package com.fluxion.sote.analysis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/** FE에서 넘어오는 분석 요청 */
public class AnalysisRequest {

    /** 분석할 일기 ID (필수) */
    private Long diaryId;

    /** 최대 10개, 각 ID는 양수 */
    @Size(max = 10)
    private List<@Positive Integer> genreIds;

    /** 선택: 메모/설명/상황 (길이 제한만) */
    @Size(max = 2000)
    private String text;

    /** FE가 보내는 날짜 (중요) */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    // ===== Getter / Setter =====
    public Long getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(Long diaryId) {
        this.diaryId = diaryId;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
