package com.fluxion.sote.challenge.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.TodayChallengeStatus;
import com.fluxion.sote.challenge.service.ChallengeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeStatusController {

    private final ChallengeStatusService statusService;

    /**
     * 챌린지 추천 여부 및 완료 여부 조회
     * date가 없으면 오늘 기준, date가 있으면 해당 날짜 기준으로 조회
     */
    @GetMapping("/status")
    public ResponseEntity<TodayChallengeStatus> getStatus(
            @RequestAttribute("user") User user,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        TodayChallengeStatus dto = statusService.getStatus(user, date);
        return ResponseEntity.ok(dto);
    }
}