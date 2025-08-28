// src/main/java/com/fluxion/sote/global/config/RedisConfig.java
package com.fluxion.sote.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // 보통 host/port는 application.yml에서 관리하는 게 좋아
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    @Primary   // 기본 RedisTemplate로 지정
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory cf) {
        RedisTemplate<String, String> rt = new RedisTemplate<>();
        rt.setConnectionFactory(cf);
        return rt;
    }
}
