package com.fluxion.sote.global.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateTimeFormatterUtil {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    // 예: "11월 17일 14:23"
    private static final DateTimeFormatter DATE_TIME_KO =
            DateTimeFormatter.ofPattern("M월 d일 HH:mm")
                    .withLocale(Locale.KOREA);

    // 예: "2025.11.17"
    private static final DateTimeFormatter DATE_DOT_KO =
            DateTimeFormatter.ofPattern("yyyy.MM.dd")
                    .withLocale(Locale.KOREA);

    private DateTimeFormatterUtil() {
    }

    public static String toKoreanDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(SEOUL).format(DATE_TIME_KO);
    }

    public static String toKoreanDateDot(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(SEOUL).toLocalDate().format(DATE_DOT_KO);
    }
}
