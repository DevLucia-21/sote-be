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
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /* ===== CORS: 운영 도메인만 허용(필요 시 추가/수정) ===== */
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "https://sote.kr",
                            "https://app.sote.kr",
                            "https://fastapi.sote.kr",   // 내부 AI 서비스 도메인(예: FastAPI)
                            "http://localhost:3000"  // 로컬 프론트(개발편의)"http://localhost:8080",  // ★ Postman 테스트용
                    ));
                    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))

                /* ===== CSRF: JWT 사용이므로 비활성화 ===== */
                .csrf(csrf -> csrf.disable())

                /* ===== 세션: 무상태 ===== */
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                /* ===== 인가 규칙 ===== */
                .authorizeHttpRequests(auth -> auth
                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 헬스체크(필수) / 루트(선택) : PaaS 라우터/모니터링용
                        .requestMatchers("/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/").permitAll()

                        // 인증 전 단계(회원/로그인/비번찾기 등)
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                                // ===== 워치 페어링 인증 =====
                        // 워치에서 최초 로그인할 때는 토큰이 없으므로 pair 엔드포인트는 permitAll
                        .requestMatchers(HttpMethod.POST, "/api/watch/auth/pair-code").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/watch/auth/pair").permitAll()

                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/genres", "/api/security-questions").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/find-email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/find-pwd").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/password-reset-temp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/check-security").permitAll()

                        // ===== 내부 서비스 간 통신(서버→서버) 결과 수신: 토큰 없음 → permitAll =====
                        .requestMatchers(HttpMethod.POST, "/api/ocr/results").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/stt/results").permitAll()

                        // ===== 워치용 건강/스트레스 API는 인증 필수 =====
                        .requestMatchers("/api/watch/health/**").authenticated()
                        .requestMatchers("/api/watch/stress/**").authenticated()


                        // ===== 사용자 호출은 인증 필수 =====
                        .requestMatchers(HttpMethod.POST, "/api/ocr/preview").authenticated() // 운영 기준: 프론트에서 토큰 전송 필수
                        .requestMatchers("/api/diaries/**").authenticated()
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/settings/**").authenticated()
                        .requestMatchers("/api/analysis/**").authenticated()
                        .requestMatchers("/api/challenge/**").authenticated()
                        .requestMatchers("/api/statistics/**").authenticated()
                        .requestMatchers("/api/questions/**").authenticated()
                        .requestMatchers("/api/stt/**").authenticated()

                        // 그 외 전부 인증
                        .anyRequest().authenticated()
                )

                /* ===== JWT 필터 체인 연결 ===== */
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        /* ===== 보안 헤더(CSP/FrameOptions) ===== */
        http.headers(headers -> {
            // 운영 프론트/리소스 정책에 맞춰 필요 시 지시어 보강 가능
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