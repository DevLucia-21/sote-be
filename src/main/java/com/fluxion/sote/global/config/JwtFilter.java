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

                // 3) 토큰에서 정보 추출
                Long userId = jwtUtil.getUserIdFromAccessToken(token);
                String role = jwtUtil.getRole(token);

                // DB에서 User 조회
                User userEntity = userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

                // 👉 challenge 컨트롤러용 (@RequestAttribute("user"))
                request.setAttribute("user", userEntity);

                // 👉 analysis 서비스용 (principal = userId)
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId, // principal은 Long
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

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
