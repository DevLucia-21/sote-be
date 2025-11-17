package com.fluxion.sote.challenge.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.ChallengeHistoryResponse;
import com.fluxion.sote.challenge.service.ChallengeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 완료된 챌린지 내역 조회 컨트롤러
 */
@RestController
@RequestMapping("/api/challenge/history")
@RequiredArgsConstructor
public class ChallengeHistoryController {

    private final ChallengeHistoryService historyService;

    /** 전체 완료된 챌린지 내역 조회 */
    @GetMapping
    public ResponseEntity<List<ChallengeHistoryResponse>> getHistory(@RequestAttribute("user") User user) {
        List<ChallengeHistoryResponse> history = historyService.getCompletedChallenges(user);
        return ResponseEntity.ok(history);
    }

    /** 월별 완료된 챌린지 내역 조회 */
    @GetMapping("/monthly")
    public ResponseEntity<List<ChallengeHistoryResponse>> getMonthlyHistory(
            @RequestAttribute("user") User user,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        List<ChallengeHistoryResponse> monthly = historyService.getMonthlyChallenges(user, year, month);
        return ResponseEntity.ok(monthly);
    }

    /** 특정 챌린지 상세 내역 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeHistoryResponse> getChallengeDetail(
            @RequestAttribute("user") User user,
            @PathVariable Long id
    ) {
        ChallengeHistoryResponse detail = historyService.getChallengeDetail(user, id);
        return ResponseEntity.ok(detail);
    }
}
