package com.fluxion.sote.diary.service.impl;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.diary.entity.WriteType;
import com.fluxion.sote.diary.repository.DiaryRepository;
import com.fluxion.sote.diary.service.DiaryService;
import com.fluxion.sote.global.enums.EmotionType;
import com.fluxion.sote.user.entity.Keyword;
import com.fluxion.sote.user.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepo;
    private final KeywordRepository keywordRepository;

    // ================== TEXT / STT 저장 ==================
    @Override
    @Transactional
    public DiaryDto write(User user, String content, LocalDate date,
                          WriteType writeType, List<Long> keywordIds, EmotionType emotionType) {
        // 미래 날짜 금지
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("미래 일기는 작성할 수 없습니다.");
        }

        diaryRepo.findByUserAndDate(user, date).ifPresent(d -> {
            throw new IllegalArgumentException("이미 작성한 일기가 있습니다.");
        });

        Set<Keyword> keywords = (keywordIds == null || keywordIds.isEmpty())
                ? Set.of()
                : keywordRepository.findAllById(keywordIds).stream().collect(Collectors.toSet());

        Diary diary = Diary.builder()
                .user(user)
                .date(date)
                .content(content)
                .writeType(writeType)     // TEXT 또는 STT
                .emotionType(emotionType)
                .keywords(keywords)
                .build();

        diaryRepo.save(diary);
        return toDto(diary);
    }

    // ================== OCR 저장 (기존 시그니처: 호환용) ==================
    @Override
    @Transactional
    public DiaryDto writeOcr(User user, String content, String imageUrl, LocalDate date) {
        return writeOcr(user, content, imageUrl, date, null, null);
    }

    // ================== OCR 저장 (확장 시그니처) ==================
    @Override
    @Transactional
    public DiaryDto writeOcr(User user, String content, String imageUrl, LocalDate date,
                             List<Long> keywordIds, EmotionType emotionType) {
        // 미래 날짜 금지
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("미래 일기는 작성할 수 없습니다.");
        }

        diaryRepo.findByUserAndDate(user, date).ifPresent(d -> {
            throw new IllegalArgumentException("이미 작성한 일기가 있습니다.");
        });

        Set<Keyword> keywords = (keywordIds == null || keywordIds.isEmpty())
                ? Set.of()
                : keywordRepository.findAllById(keywordIds).stream().collect(Collectors.toSet());

        Diary diary = Diary.builder()
                .user(user)
                .date(date)
                .content(content)
                .writeType(WriteType.OCR)   // OCR 고정
                .imageUrl(imageUrl)         // 이미지 URL 저장
                .emotionType(emotionType)   // 선택 입력
                .keywords(keywords)
                .build();

        diaryRepo.save(diary);
        return toDto(diary);
    }

    // ================== UPDATE ==================
    @Override
    @Transactional
    public DiaryDto update(User user, LocalDate date, String content,
                           List<Long> keywordIds, EmotionType emotionType) {
        // 미래 날짜 금지
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("미래 일기는 수정할 수 없습니다.");
        }

        Diary diary = diaryRepo.findByUserAndDate(user, date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 일기가 존재하지 않습니다."));

        diary.setContent(content);
        diary.setEmotionType(emotionType);

        if (keywordIds != null) {
            Set<Keyword> keywords = keywordRepository.findAllById(keywordIds).stream().collect(Collectors.toSet());
            diary.setKeywords(keywords);
        }

        return toDto(diary);
    }

    // ================== DELETE ==================
    @Override
    @Transactional
    public void delete(User user, LocalDate date) {
        Diary diary = diaryRepo.findByUserAndDate(user, date)
                .orElseThrow(() -> new IllegalArgumentException("일기가 없습니다."));
        diaryRepo.delete(diary);
    }

    // ================== 단일 조회 ==================
    @Override
    @Transactional(readOnly = true)
    public DiaryDto getByDate(User user, LocalDate date) {
        Diary diary = diaryRepo.findByUserAndDate(user, date)
                .orElseThrow(() -> new IllegalArgumentException("일기가 없습니다."));
        return toDto(diary);
    }

    // ================== 기간 조회 ==================
    @Override
    @Transactional(readOnly = true)
    public List<DiaryDto> getBetween(User user, LocalDate from, LocalDate to) {
        return diaryRepo.findAllByUserAndDateBetween(user, from, to)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ================== 키워드별 조회 ==================
    @Override
    @Transactional(readOnly = true)
    public List<DiaryDto> getByKeyword(User user, Long keywordId) {
        return diaryRepo.findByUserAndKeywordId(user, keywordId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ================== 공통 변환 메서드 ==================
    private DiaryDto toDto(Diary diary) {
        List<String> keywords = diary.getKeywords().stream()
                .map(Keyword::getContent)
                .toList();

        return new DiaryDto(
                diary.getId(),
                diary.getDate(),
                diary.getContent(),
                diary.getWriteType(),
                diary.getEmotionType(),
                diary.getImageUrl(),
                keywords
        );
    }
}
