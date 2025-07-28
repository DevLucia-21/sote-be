package com.fluxion.sote.global.util;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * SecurityContext에서 현재 로그인한 사용자 정보를 추출하는 유틸리티 클래스입니다.
 */
@Component
public class SecurityUtil {

    private static UserRepository userRepository;

    // static 메서드에서도 userRepository 사용 가능하도록 설정
    public SecurityUtil(UserRepository userRepository) {
        SecurityUtil.userRepository = userRepository;
    }

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

    /**
     * 현재 로그인한 사용자의 User 객체를 반환합니다.
     * @return User
     * @throws IllegalStateException 인증 정보가 없거나 사용자 조회에 실패한 경우
     */
    public static User getCurrentUser() {
        Long userId = getCurrentUserId();

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("로그인된 사용자를 찾을 수 없습니다."));
    }
}
