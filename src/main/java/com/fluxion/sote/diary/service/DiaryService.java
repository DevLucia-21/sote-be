package com.fluxion.sote.diary.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.entity.WriteType;
import com.fluxion.sote.global.enums.EmotionType;

import java.time.LocalDate;
import java.util.List;

public interface DiaryService {
    DiaryDto write(User user, String content, LocalDate date,
                   WriteType writeType, List<Long> keywordIds, EmotionType emotionType);

    DiaryDto update(User user, LocalDate date, String content,
                    List<Long> keywordIds, EmotionType emotionType);

    void delete(User user, LocalDate date);

    DiaryDto getByDate(User user, LocalDate date);

    // 기존 시그니처 유지 (호환용)
    DiaryDto writeOcr(User user, String content, String imageUrl, LocalDate date);

    // 확장 시그니처 (선택 필드 포함)
    DiaryDto writeOcr(User user, String content, String imageUrl, LocalDate date,
                      List<Long> keywordIds, EmotionType emotionType);

    List<DiaryDto> getBetween(User user, LocalDate from, LocalDate to);

    List<DiaryDto> getByKeyword(User user, Long keywordId);

    List<DiaryDto> getByKeywordText(User user, String keyword);
    // 오늘 일기 여부 확인
    boolean existsByDate(User user, LocalDate date);
}
