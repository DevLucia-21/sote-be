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

        // 쿠키/Authorization 허용
        cfg.setAllowCredentials(true);

        //허용할 Origin 목록 (Vercel 프론트 추가)
        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000",

                // 운영 프론트 도메인 (USER-FRONT)
                "https://sote.kr",
                "https://app.sote.kr",

                // FastAPI 내부 도메인
                "https://fastapi.sote.kr",

                //Vercel 프론트 (프론트가 배포된 실제 URL 넣기)
                "https://sote-*.vercel.app",
                "https://*.vercel.app"
        ));

        // 모든 헤더 허용
        cfg.addAllowedHeader("*");

        // 모든 HTTP 메서드 허용
        cfg.addAllowedMethod("*");

        // 프리플라이트 캐시 시간
        cfg.setMaxAge(3600L);

        // URL 매핑 등록
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

        // 필터 우선순위를 최상위로 설정 (스프링 시큐리티보다 먼저 실행)
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
