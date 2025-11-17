package com.fluxion.sote.global.config;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.exception.CustomException;
import com.fluxion.sote.global.exception.ErrorCode;
import com.fluxion.sote.global.util.JwtUtil;
import com.fluxion.sote.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redis;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil,
                     RedisTemplate<String, String> redisTemplate,
                     UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.redis = redisTemplate;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();

        /* ===============================
           1) OPTIONS 요청은 JWT 검사 생략
        =============================== */
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        /* ====================================================
           2) JWT 검증을 건너뛰는 화이트리스트 경로
        ==================================================== */
        if (isWhiteList(uri, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        /* ===============================
           3) JWT 검증
        =============================== */
        String token = resolveToken(request);

        if (token != null) {
            try {
                // access token 유효성 검증
                jwtUtil.validateAccessToken(token);

                // 블랙리스트 확인
                String jti = jwtUtil.getJti(token);
                if (Boolean.TRUE.equals(redis.hasKey("blacklist:" + jti))) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // 토큰에서 유저 정보 추출
                Long userId = jwtUtil.getUserIdFromAccessToken(token);
                String role = jwtUtil.getRole(token);

                User userEntity = userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

                request.setAttribute("user", userEntity);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception ex) {
                // JWT 오류 발생 시에도 다음 필터로 넘어감
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    /**
     * JWT 검증을 건너뛰는 경로 정의
     */
    private boolean isWhiteList(String uri, String method) {

        // 루트, 헬스체크
        if (uri.equals("/") || uri.startsWith("/health")) return true;

        // 인증 단계 (로그인, 회원가입, refresh 포함)
        if (uri.startsWith("/api/auth/")) return true;

        // 회원가입 화면 초기 데이터
        if (uri.startsWith("/api/genres")) return true;
        if (uri.startsWith("/api/security-questions")) return true;

        // 이메일/비밀번호 찾기
        if (uri.equals("/api/users/find-email")) return true;
        if (uri.equals("/api/users/find-pwd")) return true;
        if (uri.equals("/api/users/password-reset-temp")) return true;
        if (uri.equals("/api/users/check-security")) return true;

        // FastAPI ↔ Spring 콜백 (POST만)
        if (uri.equals("/api/ocr/results") && method.equals("POST")) return true;
        if (uri.equals("/api/stt/results") && method.equals("POST")) return true;

        return false;
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
