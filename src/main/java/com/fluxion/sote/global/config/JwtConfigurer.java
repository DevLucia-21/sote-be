package com.fluxion.sote.global.config;

import com.fluxion.sote.global.util.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtConfigurer
        extends AbstractHttpConfigurer<JwtConfigurer, HttpSecurity> {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redis;

    public JwtConfigurer(JwtUtil jwtUtil,
                         RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redis   = redisTemplate;
    }

    @Override
    public void configure(HttpSecurity http) {
        // JwtFilter 생성자에서 JwtUtil 과 RedisTemplate 을 모두 받도록 변경
        JwtFilter filter = new JwtFilter(jwtUtil, redis);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
