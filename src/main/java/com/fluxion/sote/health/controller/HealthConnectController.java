package com.fluxion.sote.health.controller;

import com.fluxion.sote.health.dto.DailyHealthSummaryDto;
import com.fluxion.sote.health.dto.DailyHealthSyncRequest;
import com.fluxion.sote.health.dto.DailyWaterUpdateRequest;
import com.fluxion.sote.health.dto.DailyCaffeineUpdateRequest;
import com.fluxion.sote.health.dto.DailySleepUpdateRequest;
import com.fluxion.sote.health.service.DailyHealthSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HealthConnectController {

    private final DailyHealthSummaryService service;

    // 모바일 앱 → Health Connect 요약 업로드
    @PostMapping("/mobile/health-connect/daily")
    public ResponseEntity<DailyHealthSummaryDto> syncDaily(
            @RequestBody DailyHealthSyncRequest request
    ) {
        return ResponseEntity.ok(service.syncFromMobile(request));
    }

    // 워치 → 오늘 요약 조회
    @GetMapping("/watch/health/today")
    public ResponseEntity<DailyHealthSummaryDto> getTodayForWatch() {
        return ResponseEntity.ok(service.getTodayForWatch());
    }

    // 워치 → 기간 요약 조회
    @GetMapping("/watch/health/history")
    public ResponseEntity<List<DailyHealthSummaryDto>> getHistoryForWatch(
            @RequestParam String from,
            @RequestParam String to
    ) {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        return ResponseEntity.ok(service.getRangeForWatch(fromDate, toDate));
    }

    // ============================
    // ✅ 웹앱 전용 API
    // ============================

    // 웹 → 오늘 요약 조회 (웹 건강 조회 화면에서 사용)
    @GetMapping("/health/daily/today")
    public ResponseEntity<DailyHealthSummaryDto> getTodayForWeb() {
        return ResponseEntity.ok(service.getTodayForWeb());
    }

    // 웹 → 물 섭취량 추가 (예: +200ml 버튼)
    @PostMapping("/health/daily/water")
    public ResponseEntity<DailyHealthSummaryDto> addWater(
            @RequestBody DailyWaterUpdateRequest request
    ) {
        LocalDate date = request.getDate() == null || request.getDate().isBlank()
                ? LocalDate.now()
                : LocalDate.parse(request.getDate());

        return ResponseEntity.ok(service.addWater(date, request.getAmountMl()));
    }

    // 웹 → 카페인 섭취량 추가 (예: +50mg 버튼)
    @PostMapping("/health/daily/caffeine")
    public ResponseEntity<DailyHealthSummaryDto> addCaffeine(
            @RequestBody DailyCaffeineUpdateRequest request
    ) {
        LocalDate date = request.getDate() == null || request.getDate().isBlank()
                ? LocalDate.now()
                : LocalDate.parse(request.getDate());

        return ResponseEntity.ok(service.addCaffeine(date, request.getAmountMg()));
    }

    // 웹 → 수면 시간 설정 (예: 390분 저장)
    @PostMapping("/health/daily/sleep")
    public ResponseEntity<DailyHealthSummaryDto> setSleep(
            @RequestBody DailySleepUpdateRequest request
    ) {
        LocalDate date = request.getDate() == null || request.getDate().isBlank()
                ? LocalDate.now()
                : LocalDate.parse(request.getDate());

        return ResponseEntity.ok(service.setSleepMinutes(date, request.getMinutes()));
    }
}
