package com.fluxion.sote.challenge.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.ChallengeHistoryResponse;
import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeHistoryService {

    private final UserChallengeRepository userChallengeRepo;

    /** 전체 완료된 챌린지 목록 조회 */
    @Transactional(readOnly = true)
    public List<ChallengeHistoryResponse> getCompletedChallenges(User user) {
        return userChallengeRepo.findAllByUserAndCompletedTrueOrderByCompletedAtDesc(user)
                .stream()
                .map(ChallengeHistoryResponse::fromEntity)
                .toList();
    }

    /** 월별 완료된 챌린지 목록 조회 */
    @Transactional(readOnly = true)
    public List<ChallengeHistoryResponse> getMonthlyChallenges(User user, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        return userChallengeRepo.findAllByUserAndDateBetweenAndCompletedTrueOrderByDateDesc(user, start, end)
                .stream()
                .map(ChallengeHistoryResponse::fromEntity)
                .toList();
    }

    /** 특정 챌린지 상세 내역 조회 */
    @Transactional(readOnly = true)
    public ChallengeHistoryResponse getChallengeDetail(User user, Long id) {
        UserChallenge uc = userChallengeRepo.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));
        return ChallengeHistoryResponse.fromEntity(uc);
    }
}
