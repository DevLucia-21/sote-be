package com.fluxion.sote.global.enums;

import java.util.Locale;

public enum EmotionType {
    JOY,
    SADNESS,
    ANGER,
    APATHY,
    SENSITIVE;

    /** 한국어/영문 라벨을 enum으로 매핑 (대소문자/공백/유사어 허용) */
    public static EmotionType fromLabel(String label) {
        if (label == null) return null;
        String s = label.trim().toLowerCase(Locale.ROOT);
        switch (s) {
            case "기쁨", "행복", "joy", "happy" -> { return JOY; }
            case "슬픔", "우울", "sad", "sadness" -> { return SADNESS; }
            case "분노", "화남", "anger", "angry" -> { return ANGER; }
            case "무기력", "무감정", "apathy", "neutral" -> { return APATHY; }
            case "예민", "민감", "sensitive", "nervous" -> { return SENSITIVE; }
            default -> { return null; } // 알 수 없는 라벨은 null
        }
    }

    /** enum → 기본 한국어 라벨 */
    public String toKoLabel() {
        return switch (this) {
            case JOY -> "기쁨";
            case SADNESS -> "슬픔";
            case ANGER -> "분노";
            case APATHY -> "무기력";
            case SENSITIVE -> "예민";
        };
    }
}
