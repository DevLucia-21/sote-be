package com.fluxion.sote.global.config;

import com.fluxion.sote.global.util.JwtUtil;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtConfigurer
        extends AbstractHttpConfigurer<JwtConfigurer, HttpSecurity> {

    private final JwtUtil jwtUtil;

    public JwtConfigurer(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void configure(HttpSecurity http) {
        JwtFilter filter = new JwtFilter(jwtUtil);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
