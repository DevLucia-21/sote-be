package com.fluxion.sote.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryStatsResponse {
    private int totalCount;   // 누적 작성 수
    private int monthlyCount; // 이번 달 작성 수
}
