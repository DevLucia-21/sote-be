package com.fluxion.sote.challenge.service;

import com.fluxion.sote.challenge.dto.EmotionDistributionResponse;
import com.fluxion.sote.challenge.dto.EmotionDistributionResponse.EmotionRatio;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ChallengeStatisticsServiceImpl implements ChallengeStatisticsService {

    public ChallengeStatisticsServiceImpl() {
        // 더 이상 레포지토리 주입 필요 없음
    }

    @Override
    public EmotionDistributionResponse getEmotionDistribution(String month) {
        // TODO: 실제 Diary/AI 모듈 준비되면 여기서 집계 로직으로 교체
        return new EmotionDistributionResponse(Collections.emptyList());
    }
}
