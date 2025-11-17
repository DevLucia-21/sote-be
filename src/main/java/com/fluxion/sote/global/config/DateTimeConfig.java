package com.fluxion.sote.global.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class DateTimeConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                // JSON 직렬화 시 기본 타임존을 UTC로 고정
                .timeZone(TimeZone.getTimeZone("UTC"))
                // timestamp 숫자 형태(밀리초)가 아니라 ISO-8601 문자열로 출력
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
