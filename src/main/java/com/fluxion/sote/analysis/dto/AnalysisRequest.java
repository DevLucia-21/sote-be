package com.fluxion.sote.analysis.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

/** FE에서 넘어오는 분석 요청 (장르는 선택적) */
public class AnalysisRequest {

    /** 최대 10개, 각 ID는 양수 */
    @Size(max = 10)
    private List<@Positive Integer> genreIds;

    /** 선택: 메모/설명/상황 (길이 제한만) */
    @Size(max = 2000)
    private String text;

    public List<Integer> getGenreIds() { return genreIds; }
    public void setGenreIds(List<Integer> genreIds) { this.genreIds = genreIds; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
