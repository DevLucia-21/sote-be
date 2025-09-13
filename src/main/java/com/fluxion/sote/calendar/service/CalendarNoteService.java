package com.fluxion.sote.calendar.service;

import com.fluxion.sote.analysis.entity.AnalysisResult;
import com.fluxion.sote.analysis.repository.AnalysisResultRepository;
import com.fluxion.sote.calendar.dto.CalendarNoteDto;
import com.fluxion.sote.calendar.enums.Note;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarNoteService {

    private final DiaryRepository diaryRepo;
    private final AnalysisResultRepository resultRepo;

    /**
     * 특정 사용자 월별 악보 조회
     * - Diary → AnalysisResult → Note 매핑
     * - Emotion Label + Score 함께 반환
     */
    public List<CalendarNoteDto> getMonthlyNotes(Long userId, int year, int month) {
        // 1. 해당 월 시작/끝 날짜
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // 2. 사용자 해당 월 일기 조회
        List<Diary> diaries = diaryRepo.findByUser_IdAndDateBetween(userId, start, end);

        // 3. 일기별 AnalysisResult 매핑
        return diaries.stream()
                .map(diary -> resultRepo.findByAnalysis_Diary(diary).map(result -> {
                    String label = result.getEmotionLabel();
                    double score = result.getEmotionScore() != null
                            ? result.getEmotionScore().doubleValue()
                            : 0.0;

                    Note note = Note.fromEmotion(label, score);

                    return new CalendarNoteDto(
                            diary.getDate(),
                            note,
                            label,
                            score
                    );
                }).orElse(null))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
