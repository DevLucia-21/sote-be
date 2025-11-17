package com.fluxion.sote.health.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailySleepUpdateRequest {

    // null 또는 빈 문자열이면 오늘 날짜로 처리
    private String date;    // yyyy-MM-dd
    private Long minutes;   // 수면 시간(분) - 덮어쓰기
}
