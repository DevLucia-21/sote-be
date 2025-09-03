// com.fluxion.sote.diary.dto.SttRequest.java
package com.fluxion.sote.diary.dto;

import java.time.LocalDate;

public class SttRequest {
    // 일기 저장 날짜만 받음 (자동조회 방식)
    private LocalDate date;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}