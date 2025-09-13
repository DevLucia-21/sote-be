package com.fluxion.sote.calendar.dto;

import com.fluxion.sote.calendar.enums.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class CalendarNoteDto {
    private LocalDate date;      // 일기 날짜
    private Note note;           // 매핑된 음표
    private String emotionLabel; // 감정 라벨 (예: JOY, SADNESS)
    private double score;        // 감정 점수
}
