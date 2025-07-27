package com.fluxion.sote.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityContext에서 현재 로그인한 사용자 정보를 추출하는 유틸리티 클래스입니다.
 */
public class SecurityUtil {

    private SecurityUtil() {}

    /**
     * 현재 인증된 사용자의 userId(Long)를 반환합니다.
     * principal 값은 JwtFilter에서 설정한 userId입니다.
     *
     * @return userId (Long)
     * @throws IllegalStateException 인증 정보가 없거나 형식이 맞지 않을 경우
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("현재 인증된 사용자가 없습니다.");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Long userId) {
            return userId;
        }

        throw new IllegalStateException("사용자 ID를 principal에서 찾을 수 없습니다.");
    }
}
