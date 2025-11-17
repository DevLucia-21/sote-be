package com.fluxion.sote.health.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyCaffeineUpdateRequest {

    // null 또는 빈 문자열이면 오늘 날짜로 처리
    private String date;      // yyyy-MM-dd
    private Double amountMg;  // 추가할 카페인 섭취량 (mg)
}
