package com.fluxion.sote.global.config;

import com.fluxion.sote.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // ✅ 추가
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redis;

    public JwtFilter(JwtUtil jwtUtil,
                     RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redis = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization 헤더에서 Bearer 토큰 추출
        String token = resolveToken(request);

        if (token != null) {
            // 1) Access Token 서명 및 만료 검증
            try {
                jwtUtil.validateAccessToken(token);
            } catch (Exception ex) {
                // 토큰이 유효하지 않으면 필터 체인 다음 필터로 넘어감 (401은 AuthenticationEntryPoint에서 처리)
                filterChain.doFilter(request, response);
                return;
            }

            // 2) 토큰에서 jti 추출 후 블랙리스트 여부 확인
            String jti = jwtUtil.getJti(token);
            if (Boolean.TRUE.equals(redis.hasKey("blacklist:" + jti))) {
                // 블랙리스트에 있으면 인증 거부
                filterChain.doFilter(request, response);
                return;
            }

            // 3) 토큰에서 사용자 정보 추출 (sub → userId, claim → role)
            Long userId = jwtUtil.getUserIdFromAccessToken(token);
            System.out.println("JwtFilter 인증된 userId = " + userId); // 추가
            String role = jwtUtil.getRole(token);

            // ✅ 4) 인증 객체 생성 시 권한을 SimpleGrantedAuthority로 명확히 지정
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userId, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role)) // ✅ ROLE_ 접두사 붙임
                    );

            // ✅ 5) SecurityContext에 등록
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰을 추출하는 메서드
     * 예: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // "Bearer " 이후의 실제 토큰만 추출
        }
        return null;
    }
}
