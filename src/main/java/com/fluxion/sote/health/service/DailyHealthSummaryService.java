package com.fluxion.sote.health.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.health.dto.DailyHealthSummaryDto;
import com.fluxion.sote.health.dto.DailyHealthSyncRequest;
import com.fluxion.sote.health.entity.DailyHealthSummary;
import com.fluxion.sote.health.repository.DailyHealthSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class DailyHealthSummaryService {

    private final DailyHealthSummaryRepository repository;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /**
     * 모바일 앱(또는 Health Connect)에서 하루 요약 전체 동기화
     */
    public DailyHealthSummaryDto syncFromMobile(DailyHealthSyncRequest request) {
        User user = SecurityUtil.getCurrentUser();
        LocalDate date = LocalDate.parse(request.getDate());

        DailyHealthSummary summary = repository
                .findByUserAndDate(user, date)
                .orElseGet(() -> DailyHealthSummary.builder()
                        .user(user)
                        .date(date)
                        .build());

        summary.setSteps(request.getSteps());
        summary.setAvgHeartRate(request.getAvgHeartRate());
        summary.setAvgHrvRmssd(request.getAvgHrvRmssd());
        summary.setSleepMinutes(request.getSleepMinutes());
        summary.setWaterMl(request.getWaterMl());
        summary.setCaffeineMg(request.getCaffeineMg());

        repository.save(summary);

        return toDto(summary);
    }

    /**
     * 워치용: 오늘 요약 조회
     */
    public DailyHealthSummaryDto getTodayForWatch() {
        User user = SecurityUtil.getCurrentUser();
        LocalDate today = LocalDate.now(KST);

        DailyHealthSummary summary = repository
                .findByUserAndDate(user, today)
                .orElse(null);

        if (summary == null) {
            return DailyHealthSummaryDto.builder()
                    .date(today.toString())
                    .build();
        }
        return toDto(summary);
    }

    /**
     * 웹용: 오늘 요약 조회 (엔드포인트만 다르고 로직은 동일하게 사용)
     */
    public DailyHealthSummaryDto getTodayForWeb() {
        return getTodayForWatch();
    }

    /**
     * 워치용: 기간 요약 조회
     */
    public List<DailyHealthSummaryDto> getRangeForWatch(LocalDate from, LocalDate to) {
        User user = SecurityUtil.getCurrentUser();
        return repository.findAllByUserAndDateBetween(user, from, to)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 웹용: 특정 날짜에 물 섭취량 추가
     */
    public DailyHealthSummaryDto addWater(LocalDate date, Double amountMl) {
        if (amountMl == null || amountMl <= 0) {
            throw new IllegalArgumentException("amountMl must be > 0");
        }

        User user = SecurityUtil.getCurrentUser();

        DailyHealthSummary summary = repository
                .findByUserAndDate(user, date)
                .orElseGet(() -> DailyHealthSummary.builder()
                        .user(user)
                        .date(date)
                        .build());

        Double current = summary.getWaterMl() == null ? 0.0 : summary.getWaterMl();
        summary.setWaterMl(current + amountMl);

        repository.save(summary);
        return toDto(summary);
    }

    /**
     * 웹용: 특정 날짜에 카페인 섭취량 추가
     */
    public DailyHealthSummaryDto addCaffeine(LocalDate date, Double amountMg) {
        if (amountMg == null || amountMg <= 0) {
            throw new IllegalArgumentException("amountMg must be > 0");
        }

        User user = SecurityUtil.getCurrentUser();

        DailyHealthSummary summary = repository
                .findByUserAndDate(user, date)
                .orElseGet(() -> DailyHealthSummary.builder()
                        .user(user)
                        .date(date)
                        .build());

        Double current = summary.getCaffeineMg() == null ? 0.0 : summary.getCaffeineMg();
        summary.setCaffeineMg(current + amountMg);

        repository.save(summary);
        return toDto(summary);
    }

    /**
     * 웹용: 특정 날짜 수면 시간 설정(덮어쓰기)
     */
    public DailyHealthSummaryDto setSleepMinutes(LocalDate date, Long minutes) {
        if (minutes == null || minutes < 0) {
            throw new IllegalArgumentException("minutes must be >= 0");
        }

        User user = SecurityUtil.getCurrentUser();

        DailyHealthSummary summary = repository
                .findByUserAndDate(user, date)
                .orElseGet(() -> DailyHealthSummary.builder()
                        .user(user)
                        .date(date)
                        .build());

        summary.setSleepMinutes(minutes);

        repository.save(summary);
        return toDto(summary);
    }

    private DailyHealthSummaryDto toDto(DailyHealthSummary entity) {
        return DailyHealthSummaryDto.builder()
                .id(entity.getId())
                .date(entity.getDate().toString())
                .steps(entity.getSteps())
                .avgHeartRate(entity.getAvgHeartRate())
                .avgHrvRmssd(entity.getAvgHrvRmssd())
                .sleepMinutes(entity.getSleepMinutes())
                .waterMl(entity.getWaterMl())
                .caffeineMg(entity.getCaffeineMg())
                .build();
    }
}
