package com.fluxion.sote.global.config;

import com.fluxion.sote.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        this.redis   = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // 1) Access Token 서명·만료 검증
            try {
                jwtUtil.validateAccessToken(token);
            } catch (Exception ex) {
                // 토큰 유효하지 않으면 바로 다음 필터로 (401 처리는 AuthenticationEntryPoint에서)
                filterChain.doFilter(request, response);
                return;
            }

            // 2) jti 추출 후 블랙리스트 체크
            String jti = jwtUtil.getJti(token);
            if (Boolean.TRUE.equals(redis.hasKey("blacklist:" + jti))) {
                // 블랙리스트에 있으면 인증 거부
                filterChain.doFilter(request, response);
                return;
            }

            // 3) 사용자 정보 추출 (subject → userId, claim → role)
            Long userId = jwtUtil.getUserIdFromAccessToken(token);
            String role = jwtUtil.getRole(token);

            // 4) Spring SecurityContext에 인증 세팅
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userId, null,
                            List.of(() -> role)
                    );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
