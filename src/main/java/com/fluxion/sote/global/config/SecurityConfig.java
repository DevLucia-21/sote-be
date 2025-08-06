package com.fluxion.sote.global.config;

import com.fluxion.sote.global.config.JwtFilter;
import com.fluxion.sote.global.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CORS 처리: WebConfig에 등록된 CorsFilter 사용
                .cors(Customizer.withDefaults())

                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // JWT Stateless 세션 정책
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers("/api/auth/**", "/api/users/**", "/api/settings/**", "/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/genres", "/api/security-questions").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/diaries").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/diaries").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/diaries").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/diaries").authenticated()
                        .anyRequest().authenticated()
                )

                // JWT 필터를 스프링 시큐리티 필터 체인 앞단에 삽입
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
