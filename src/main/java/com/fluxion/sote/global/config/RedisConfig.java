package com.fluxion.sote.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // 환경변수 or application.yml로 호스트/포트 설정 가능
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory cf) {
        RedisTemplate<String, String> rt = new RedisTemplate<>();
        rt.setConnectionFactory(cf);
        return rt;
    }
}
