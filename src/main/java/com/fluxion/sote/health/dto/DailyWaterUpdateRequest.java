package com.fluxion.sote.health.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyWaterUpdateRequest {

    // null 또는 빈 문자열이면 오늘 날짜로 처리
    private String date;      // yyyy-MM-dd
    private Double amountMl;  // 추가할 물 섭취량 (ml)
}
