package com.fluxion.sote.diary.service.impl;

import com.fluxion.sote.analysis.service.AnalysisService;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.diary.entity.WriteType;
import com.fluxion.sote.diary.event.DiarySavedEvent;
import com.fluxion.sote.diary.repository.DiaryRepository;
import com.fluxion.sote.diary.service.DiaryService;
import com.fluxion.sote.global.enums.EmotionType;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.user.entity.Keyword;
import com.fluxion.sote.user.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepo;
    private final KeywordRepository keywordRepository;
    private final AnalysisService analysisService;
    private final ApplicationEventPublisher publisher;

    // 🔥 한국 기준 오늘 날짜 계산용 KST 타임존
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    //사용자 소유 키워드만 검증
    //키워드 개수 제한: 작성 시 최대 5개
    private Set<Keyword> validateAndGetKeywords(User user, List<Long> keywordIds) {
        if (keywordIds == null || keywordIds.isEmpty()) {
            return Set.of();
        }

        if (keywordIds.size() > 5) {
            throw new IllegalArgumentException("키워드는 최대 5개까지만 선택할 수 있습니다.");
        }

        List<Keyword> keywords = keywordRepository.findAllByIdInAndUser(keywordIds, user);
        if (keywords.size() != keywordIds.size()) {
            throw new IllegalArgumentException("자신의 키워드만 선택할 수 있습니다.");
        }

        return Set.copyOf(keywords);
    }

    // ================== TEXT / STT 저장 ==================
    @Override
    @Transactional
    public DiaryDto write(User user, String content, LocalDate date,
                          WriteType writeType, List<Long> keywordIds, EmotionType emotionType) {

        LocalDate todayKst = LocalDate.now(KST);
        if (date.isAfter(todayKst)) {
            throw new IllegalArgumentException("미래 일기는 작성할 수 없습니다.");
        }

        diaryRepo.findByUserAndDate(user, date).ifPresent(d -> {
            throw new IllegalArgumentException("이미 작성한 일기가 있습니다.");
        });

        Set<Keyword> keywords = validateAndGetKeywords(user, keywordIds);

        Diary diary = Diary.builder()
                .user(user)
                .date(date)
                .content(content)
                .writeType(writeType)
                .emotionType(emotionType)
                .keywords(keywords)
                .build();

        diaryRepo.saveAndFlush(diary); // ID 확정

        // 커밋 완료 후 자동 감정분석 실행 이벤트 발행
        publisher.publishEvent(new DiarySavedEvent(diary));

        return toDto(diary);
    }

    // ================== OCR 저장 (호환용) ==================
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

        LocalDate todayKst = LocalDate.now(KST);
        if (date.isAfter(todayKst)) {
            throw new IllegalArgumentException("미래 일기는 작성할 수 없습니다.");
        }

        diaryRepo.findByUserAndDate(user, date).ifPresent(d -> {
            throw new IllegalArgumentException("이미 작성한 일기가 있습니다.");
        });

        Set<Keyword> keywords = validateAndGetKeywords(user, keywordIds);

        Diary diary = Diary.builder()
                .user(user)
                .date(date)
                .content(content)
                .writeType(WriteType.OCR)
                .imageUrl(imageUrl)
                .emotionType(emotionType)
                .keywords(keywords)
                .build();

        diaryRepo.saveAndFlush(diary);

        // 커밋 완료 후 자동 감정분석 실행 이벤트 발행
        publisher.publishEvent(new DiarySavedEvent(diary));

        return toDto(diary);
    }

    // ================== UPDATE ==================
    @Override
    @Transactional
    public DiaryDto update(User user, LocalDate date, String content,
                           List<Long> keywordIds, EmotionType emotionType) {

        LocalDate todayKst = LocalDate.now(KST);
        if (date.isAfter(todayKst)) {
            throw new IllegalArgumentException("미래 일기는 수정할 수 없습니다.");
        }

        Diary diary = diaryRepo.findByUserAndDate(user, date)
                .orElseThrow(() -> new ResourceNotFoundException("해당 날짜의 일기가 존재하지 않습니다."));

        diary.setContent(content);
        diary.setEmotionType(emotionType);

        if (keywordIds != null) {
            Set<Keyword> keywords = validateAndGetKeywords(user, keywordIds);
            diary.setKeywords(keywords);
        }

        diaryRepo.saveAndFlush(diary);

        // 커밋 완료 후 자동 감정분석 실행 이벤트 발행
        publisher.publishEvent(new DiarySavedEvent(diary));

        return toDto(diary);
    }

    // ================== DELETE ==================
    @Override
    @Transactional
    public void delete(User user, LocalDate date) {
        Diary diary = diaryRepo.findByUserAndDate(user, date)
                .orElseThrow(() -> new ResourceNotFoundException("해당 날짜의 일기가 존재하지 않습니다."));
        diaryRepo.delete(diary);
    }

    // ================== 단일 조회 ==================
    @Override
    @Transactional(readOnly = true)
    public DiaryDto getByDate(User user, LocalDate date) {
        Diary diary = diaryRepo.findByUserAndDate(user, date)
                .orElseThrow(() -> new ResourceNotFoundException("해당 날짜의 일기가 존재하지 않습니다."));
        return toDto(diary);
    }

    // ================== 오늘 일기 여부 확인 ==================
    @Override
    @Transactional(readOnly = true)
    public boolean existsByDate(User user, LocalDate date) {
        return diaryRepo.findByUserAndDate(user, date).isPresent();
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
        Keyword keyword = keywordRepository.findByIdAndUser(keywordId, user)
                .orElseThrow(() -> new ResourceNotFoundException("해당 키워드를 찾을 수 없습니다."));

        return diaryRepo.findAllByUserAndKeywordsContaining(user, keyword)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ================== 키워드 텍스트 조회 ==================
    @Override
    @Transactional(readOnly = true)
    public List<DiaryDto> getByKeywordText(User user, String keyword) {
        return diaryRepo.findByKeywordText(user, keyword)
                .stream()
                .map(this::toDto)
                .toList();
    }

    //다중 키워드 검색 (검색 시 최대 3개 제한)
    @Override
    @Transactional(readOnly = true)
    public List<DiaryDto> getByKeywords(User user, List<Long> keywordIds, String mode) {
        if (keywordIds == null || keywordIds.isEmpty()) {
            throw new IllegalArgumentException("검색할 키워드가 없습니다.");
        }
        if (keywordIds.size() > 3) {
            throw new IllegalArgumentException("검색은 최대 3개의 키워드까지만 가능합니다.");
        }

        List<Diary> diaries;
        if ("and".equalsIgnoreCase(mode)) {
            diaries = diaryRepo.findAllByUserAndAllKeywords(user, keywordIds, keywordIds.size());
        } else {
            diaries = diaryRepo.findAllByUserAndKeywordsIn(user, keywordIds);
        }

        return diaries.stream()
                .map(this::toDto)
                .toList();
    }

    // ================== DTO 변환 ==================
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

    @Override
    @Transactional
    public DiaryDto writeCanvas(User user, String content, String canvasImageBase64,
                                LocalDate date, List<Long> keywordIds, EmotionType emotionType) {

        if (date == null) {
            date = LocalDate.now(KST);
        }

        LocalDate todayKst = LocalDate.now(KST);
        if (date.isAfter(todayKst)) {
            throw new IllegalArgumentException("미래 일기는 작성할 수 없습니다.");
        }

        diaryRepo.findByUserAndDate(user, date).ifPresent(d -> {
            throw new IllegalArgumentException("이미 작성한 일기가 있습니다.");
        });

        Set<Keyword> keywords = validateAndGetKeywords(user, keywordIds);

        // base64 → 파일 변환
        String imageUrl = null;
        if (canvasImageBase64 != null && !canvasImageBase64.isBlank()) {
            try {
                byte[] imageBytes = java.util.Base64.getDecoder().decode(
                        canvasImageBase64.replaceFirst("^data:image/[^;]+;base64,", "")
                );
                java.nio.file.Path tempDir = java.nio.file.Files.createTempDirectory("canvas_");
                java.nio.file.Path imagePath = tempDir.resolve("canvas_" + System.currentTimeMillis() + ".png");
                java.nio.file.Files.write(imagePath, imageBytes);
                imageUrl = imagePath.toString(); // TODO: 이후 S3나 CDN 업로드 시 교체
            } catch (Exception e) {
                throw new RuntimeException("Canvas 이미지 저장 중 오류 발생", e);
            }
        }

        Diary diary = Diary.builder()
                .user(user)
                .date(date)
                .content(content)
                .writeType(WriteType.OCR)
                .imageUrl(imageUrl)
                .emotionType(emotionType)
                .keywords(keywords)
                .build();

        diaryRepo.saveAndFlush(diary);
        publisher.publishEvent(new DiarySavedEvent(diary));

        return toDto(diary);
    }

}
