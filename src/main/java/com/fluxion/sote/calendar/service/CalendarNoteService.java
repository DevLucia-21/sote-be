package com.fluxion.sote.calendar.service;

import com.fluxion.sote.analysis.repository.AnalysisResultRepository;
import com.fluxion.sote.calendar.dto.CalendarNoteDto;
import com.fluxion.sote.calendar.enums.Note;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.diary.repository.DiaryRepository;
import com.fluxion.sote.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarNoteService {

    private final DiaryRepository diaryRepo;
    private final AnalysisResultRepository resultRepo;

    /**
     * 월별 조회
     */
    public List<CalendarNoteDto> getMonthlyNotes(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        User user = new User();
        user.setId(userId);

        List<Diary> diaries = diaryRepo.findAllByUserAndDateBetween(user, start, end);
        return mapToDtos(diaries);
    }

    /**
     * 하루 조회
     */
    public CalendarNoteDto getDailyNote(Long userId, int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);

        User user = new User();
        user.setId(userId);
        Optional<Diary> diaryOpt = diaryRepo.findByUserAndDate(user, date);

        return diaryOpt.flatMap(diary ->
                resultRepo.findByAnalysis_Diary(diary)
                        .map(result -> new CalendarNoteDto(
                                diary.getDate(),
                                Note.fromEmotion(result.getEmotionLabel(),
                                        result.getEmotionScore() != null ? result.getEmotionScore().doubleValue() : 0.0),
                                result.getEmotionLabel(),
                                result.getEmotionScore() != null ? result.getEmotionScore().doubleValue() : 0.0
                        ))
        ).orElse(null);
    }

    /**
     * 주간 조회
     */
    public List<CalendarNoteDto> getWeeklyNotes(Long userId, int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalDate start = date.with(DayOfWeek.MONDAY);
        LocalDate end = date.with(DayOfWeek.SUNDAY);

        User user = new User();
        user.setId(userId);

        List<Diary> diaries = diaryRepo.findAllByUserAndDateBetween(user, start, end);
        return mapToDtos(diaries);
    }

    /**
     * Diary → CalendarNoteDto 변환 유틸
     */
    private List<CalendarNoteDto> mapToDtos(List<Diary> diaries) {
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
