package com.fluxion.sote.diary.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.entity.WriteType;
import com.fluxion.sote.diary.service.DiaryService;
import com.fluxion.sote.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    private User getCurrentUser() {
        return SecurityUtil.getCurrentUser();
    }

    @PostMapping
    public ResponseEntity<DiaryDto> write(@RequestBody DiaryDto.CreateRequest request) {
        User user = getCurrentUser();
        DiaryDto dto = diaryService.write(
                user,
                request.getContent(),
                request.getDate(),
                WriteType.TEXT,
                request.getKeywordIds(),
                request.getEmotionType()
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/stt")
    public ResponseEntity<DiaryDto> writeFromStt(@RequestBody DiaryDto.CreateRequest request) {
        User user = getCurrentUser();
        DiaryDto dto = diaryService.write(
                user,
                request.getContent(),
                request.getDate(),
                WriteType.STT,
                request.getKeywordIds(),
                request.getEmotionType()
        );
        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<DiaryDto> update(@RequestBody DiaryDto.UpdateRequest request) {
        User user = getCurrentUser();
        DiaryDto dto = diaryService.update(
                user,
                request.getDate(),
                request.getContent(),
                request.getKeywordIds(),
                request.getEmotionType()
        );
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam("date")
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                       LocalDate date) {
        User user = getCurrentUser();
        diaryService.delete(user, date);
        return ResponseEntity.ok().build();
    }

    /** 오늘 일기 여부 확인 */
    @GetMapping("/today/exist")
    public ResponseEntity<Boolean> checkTodayDiary() {
        User user = getCurrentUser();
        boolean exists = diaryService.existsByDate(user, LocalDate.now());
        return ResponseEntity.ok(exists);
    }


    @GetMapping(params = "date")
    public ResponseEntity<DiaryDto> getByDate(@RequestParam("date")
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                              LocalDate date) {
        User user = getCurrentUser();
        return ResponseEntity.ok(diaryService.getByDate(user, date));
    }

    @GetMapping(params = {"from", "to"})
    public ResponseEntity<List<DiaryDto>> getByRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        User user = getCurrentUser();
        return ResponseEntity.ok(diaryService.getBetween(user, from, to));
    }

    @GetMapping("/keyword/{keywordId}")
    public ResponseEntity<List<DiaryDto>> getByKeyword(@PathVariable Long keywordId) {
        User user = getCurrentUser();
        return ResponseEntity.ok(diaryService.getByKeyword(user, keywordId));
    }

    @GetMapping("/keyword/search")
    public ResponseEntity<List<DiaryDto>> getByKeywordText(@RequestParam String keyword) {
        User user = getCurrentUser();
        return ResponseEntity.ok(diaryService.getByKeywordText(user, keyword));
    }

    //다중 키워드 검색 (mode=any 또는 all)
    @GetMapping("/keywords")
    public ResponseEntity<List<DiaryDto>> getByMultipleKeywords(
            @RequestParam("ids") List<Long> keywordIds,
            @RequestParam(value = "mode", required = false, defaultValue = "any") String mode) {

        User user = getCurrentUser();
        List<DiaryDto> results = diaryService.getByKeywords(user, keywordIds, mode);
        return ResponseEntity.ok(results);
    }

}
