package com.fluxion.sote.challenge.service;

import com.fluxion.sote.challenge.dto.EmotionDistributionResponse;

public interface ChallengeStatisticsService {
    EmotionDistributionResponse getEmotionDistribution(String month);
}
