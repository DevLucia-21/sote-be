package com.fluxion.sote.global.util;

import java.time.LocalDate;

public final class BirthYearUtil {
    private BirthYearUtil() {}

    /** 생년월일 → 출생연도 */
    public static int extractYear(LocalDate birthDate) {
        if (birthDate == null) return -1;
        return birthDate.getYear();
    }
}
