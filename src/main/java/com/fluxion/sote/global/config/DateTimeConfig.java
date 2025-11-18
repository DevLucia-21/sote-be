package com.fluxion.sote.global.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class DateTimeConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                .modulesToInstall(new JavaTimeModule())  //LocalDate / LocalDateTime 지원
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    }
}
