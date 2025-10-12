package com.fluxion.sote.global.config;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.user.repository.UserRepository;
import com.fluxion.sote.global.exception.CustomException;
import com.fluxion.sote.global.exception.ErrorCode;
import com.fluxion.sote.global.util.JwtUtil;
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

        String uri = request.getRequestURI();

        // JWT 검증을 건너뛰어야 하는 경로 (서버 간 통신, 헬스체크)
        if (uri.startsWith("/api/ocr/results") ||
                uri.startsWith("/api/stt/results") ||
                uri.startsWith("/health") ||
                uri.equals("/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ----------------------------
        // JWT 검증 시작
        // ----------------------------
        String token = resolveToken(request);

        if (token != null) {
            try {
                // 1) Access Token 검증
                jwtUtil.validateAccessToken(token);

                // 2) 블랙리스트 확인
                String jti = jwtUtil.getJti(token);
                if (Boolean.TRUE.equals(redis.hasKey("blacklist:" + jti))) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // 3) 토큰 정보 추출
                Long userId = jwtUtil.getUserIdFromAccessToken(token);
                String role = jwtUtil.getRole(token);

                // 4) DB에서 User 조회
                User userEntity = userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

                // Controller 단에서 user 직접 접근 가능하도록
                request.setAttribute("user", userEntity);

                // 인증 객체 생성 및 SecurityContext 등록
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception ex) {
                // 토큰 오류 발생 시 다음 필터로 넘김 (403 직접 던지지 않음)
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 토큰이 없거나 인증 실패한 경우에도 계속 체인 진행
        filterChain.doFilter(request, response);
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
