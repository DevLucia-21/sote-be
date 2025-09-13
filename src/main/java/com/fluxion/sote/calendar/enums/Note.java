package com.fluxion.sote.calendar.enums;

public enum Note {
    DO, RE, MI, FA, SOL, LA, SI,
    HDO, HRE, HMI, HFA, HSOL, HLA, HSI;

    /**
     * 감정 라벨과 점수를 기반으로 음표 결정
     * - 기준값 = 3.5
     * - 기준 이상이면 H 붙은 높은 음 반환
     */
    public static Note fromEmotion(String emotionLabel, double score) {
        double threshold = 3.5;
        if (emotionLabel == null) return DO;

        switch (emotionLabel.trim()) {
            // SADNESS
            case "SADNESS":
            case "슬픔":
                return score < threshold ? DO : HDO;

            // APATHY
            case "APATHY":
            case "무기력":
                return score < threshold ? MI : HMI;

            // SENSITIVE
            case "SENSITIVE":
            case "예민":
                return score < threshold ? SOL : HSOL;

            // ANGER
            case "ANGER":
            case "화남":
                return score < threshold ? SI : HSI;

            // JOY
            case "JOY":
            case "기쁨":
                return score < threshold ? RE : HRE;

            default:
                return DO;
        }
    }

}
