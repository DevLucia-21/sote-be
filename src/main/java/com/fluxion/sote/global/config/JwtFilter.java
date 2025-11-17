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

        /* 1) OPTIONS 요청 */
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        /* 2) JWT 검사 제외 경로 */
        if (isWhiteList(uri, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        /* 3) JWT 검사 */
        String token = resolveToken(request);

        if (token != null) {
            try {
                jwtUtil.validateAccessToken(token);

                String jti = jwtUtil.getJti(token);
                if (Boolean.TRUE.equals(redis.hasKey("blacklist:" + jti))) {
                    filterChain.doFilter(request, response);
                    return;
                }

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
                filterChain.doFilter(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /** 화이트리스트 */
    private boolean isWhiteList(String uri, String method) {

        if (uri.equals("/") || uri.startsWith("/health")) return true;

        if (uri.startsWith("/api/auth/")) return true;

        if (uri.startsWith("/api/genres")) return true;
        if (uri.startsWith("/api/security-questions")) return true;

        if (uri.equals("/api/users/find-email")) return true;
        if (uri.equals("/api/users/find-pwd")) return true;
        if (uri.equals("/api/users/password-reset-temp")) return true;
        if (uri.equals("/api/users/check-security")) return true;

        // FastAPI callbacks
        if (uri.equals("/api/ocr/results") && method.equals("POST")) return true;
        if (uri.equals("/api/stt/results") && method.equals("POST")) return true;

        // 워치 로그인 (JWT 없이 동작해야 함)
        if (uri.equals("/api/watch/auth/pair")) return true;

        return false;
    }

    /** Authorization: Bearer 토큰 파싱 */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
