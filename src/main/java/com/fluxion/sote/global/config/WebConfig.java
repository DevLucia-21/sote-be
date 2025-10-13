package com.fluxion.sote.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class WebConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // 자격 증명(쿠키, Authorization 헤더 등)을 허용
        cfg.setAllowCredentials(true);

        // 허용할 Origin 패턴들 (로컬 + 운영 + 내부 FastAPI)
        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",      // React 로컬 개발
                "http://127.0.0.1:3000",      // 일부 브라우저 환경
                "https://sote.kr",             // 운영 프론트
                "https://app.sote.kr",         // 서브도메인(앱 프론트)
                "https://fastapi.sote.kr"      // 내부 AI FastAPI 서버
        ));

        // 모든 헤더 허용
        cfg.addAllowedHeader("*");

        // 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE, OPTIONS 등)
        cfg.addAllowedMethod("*");

        // preflight 캐시 시간 (초 단위) — 선택사항
        cfg.setMaxAge(3600L);

        // URL 매핑
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);

        return src;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(
            @Qualifier("corsConfigurationSource") CorsConfigurationSource source
    ) {
        CorsFilter filter = new CorsFilter(source);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(filter);

        // 필터 우선순위를 가장 높게 설정
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
