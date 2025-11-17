package com.fluxion.sote.global.config;

import com.fluxion.sote.global.config.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                /* ===== CORS (WebConfig에서 설정한 도메인 허용) ===== */
                .cors(Customizer.withDefaults())

                /* ===== CSRF OFF ===== */
                .csrf(csrf -> csrf.disable())

                /* ===== Stateless ===== */
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                /* ===== 인가 규칙 ===== */
                .authorizeHttpRequests(auth -> auth

                        /* ===== Preflight CORS 허용 ===== */
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        /* ===== Health 체크 ===== */
                        .requestMatchers("/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/").permitAll()

                        /* ===== 로그인/회원가입 ===== */
                        .requestMatchers("/api/auth/**").permitAll()

                        /* ===== 회원가입 화면용 데이터 ===== */
                        .requestMatchers(HttpMethod.GET, "/api/genres").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/security-questions").permitAll()

                        /* ===== 사용자 계정 관련 (로그인 전) ===== */
                        .requestMatchers(HttpMethod.POST, "/api/users/find-email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/find-pwd").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/password-reset-temp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/check-security").permitAll()

                        /* ===== FastAPI ↔ Spring Server (콜백) ===== */
                        .requestMatchers(HttpMethod.POST, "/api/ocr/results").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/stt/results").permitAll()

                        /* ===== 그 외 모든 /api/** 는 인증 필요 ===== */
                        .requestMatchers("/api/**").authenticated()

                        .anyRequest().authenticated()
                )

                /* ===== JWT 필터 ===== */
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                /* ===== 보안 헤더 ===== */
                .headers(headers -> {
                    headers.contentSecurityPolicy(csp ->
                            csp.policyDirectives("default-src 'self'")
                    );
                    headers.frameOptions(frame -> frame.sameOrigin());
                });

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
