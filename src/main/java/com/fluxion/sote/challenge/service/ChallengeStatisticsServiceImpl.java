package com.fluxion.sote.challenge.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.EmotionDistributionResponse;
import com.fluxion.sote.challenge.dto.EmotionDistributionResponse.EmotionRatio;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import com.fluxion.sote.global.enums.EmotionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChallengeStatisticsServiceImpl implements ChallengeStatisticsService {

    private final UserChallengeRepository userChallengeRepo;

    @Override
    public EmotionDistributionResponse getEmotionDistribution(String month) {
        // "YYYY-MM" → YearMonth 변환
        YearMonth yearMonth = YearMonth.parse(month);

        // 해당 월 시작/끝 날짜
        var startDate = yearMonth.atDay(1);
        var endDate = yearMonth.atEndOfMonth();

        // 감정별 카운트 집계
        Map<EmotionType, Long> counts = new EnumMap<>(EmotionType.class);
        for (EmotionType type : EmotionType.values()) {
            long count = userChallengeRepo
                    .countByChallenge_EmotionTypeAndDateBetweenAndCompletedTrue(type, startDate, endDate);
            counts.put(type, count);
        }

        long total = counts.values().stream().mapToLong(Long::longValue).sum();

        // 퍼센트 변환
        List<EmotionRatio> ratios = new ArrayList<>();
        if (total > 0) {
            for (var entry : counts.entrySet()) {
                double percentage = (entry.getValue() * 100.0) / total;
                ratios.add(new EmotionRatio(entry.getKey(), percentage));
            }
        }

        return new EmotionDistributionResponse(ratios);
    }
}
