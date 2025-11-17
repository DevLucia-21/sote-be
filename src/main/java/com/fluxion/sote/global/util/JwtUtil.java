package com.fluxion.sote.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 발급 및 검증을 위한 유틸리티 클래스입니다.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration-ms}")
    private long accessMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshMs;

    private Key key;
    private JwtParser parser;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }

    /**
     * Access Token 생성 (stateless)
     */
    public String createAccessToken(Long userId, String role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성 (with jti for rotation)
     */
    public String createRefreshToken(Long userId, String role) {
        Date now = new Date();
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .setId(jti)
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 주어진 토큰을 검증 (서명 및 만료)
     */
    private Claims parseClaims(String token) {
        return parser.parseClaimsJws(token).getBody();
    }

    /**
     * Access Token 검증
     */
    public void validateAccessToken(String token) {
        parseClaims(token);
    }

    /**
     * Refresh Token 검증
     */
    public void validateRefreshToken(String token) {
        parseClaims(token);
    }

    /**
     * Access Token에서 userId 추출
     */
    public Long getUserIdFromAccessToken(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    /**
     * Refresh Token의 jti 추출
     */
    public String getJti(String token) {
        return parseClaims(token).getId();
    }

    /**
     * Refresh Token에서 userId 추출
     */
    public Long getUserIdFromRefreshToken(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    /**
     * 토큰에서 role 클레임 추출
     */
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);

    }

    /**
     * Access Token 만료 시간(ms) 조회
     */
    public long getAccessExpiry() {
        return accessMs;
    }

    /**
     * Refresh Token 만료 시간(ms) 조회
     */
    public long getRefreshExpiry() {
        return refreshMs;
    }
}
