package com.fluxion.sote.challenge.dto;

import com.fluxion.sote.global.enums.EmotionType;
import java.util.List;

public class EmotionDistributionResponse {

    private List<EmotionRatio> ratios;

    public EmotionDistributionResponse(List<EmotionRatio> ratios) {
        this.ratios = ratios;
    }

    public List<EmotionRatio> getRatios() {
        return ratios;
    }

    public void setRatios(List<EmotionRatio> ratios) {
        this.ratios = ratios;
    }

    public static class EmotionRatio {
        private EmotionType emotion;
        private double percentage; // 0.0 ~ 100.0

        public EmotionRatio(EmotionType emotion, double percentage) {
            this.emotion = emotion;
            this.percentage = percentage;
        }

        public EmotionType getEmotion() {
            return emotion;
        }

        public double getPercentage() {
            return percentage;
        }
    }
}
