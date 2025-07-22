package com.fluxion.sote.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration-ms}")
    private long accessMs;      // 예: 3600000 (1시간)

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshMs;     // 예: 86400000 (24시간)

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
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
                .setId(jti)                         // jti 클레임에 고유 ID 삽입
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Access Token 서명·만료 검증
     */
    public void validateAccessToken(String token) {
        parser().parseClaimsJws(token);
    }

    /**
     * Refresh Token 서명·만료 검증 (ID 검증은 서비스 레이어에서 처리)
     */
    public void validateRefreshToken(String token) {
        parser().parseClaimsJws(token);
    }

    /**
     * 주어진 Access Token의 subject(userId) 반환
     */
    public Long getUserIdFromAccessToken(String token) {
        Claims claims = parser().parseClaimsJws(token).getBody();
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 주어진 Refresh Token의 jti(claim) 추출
     */
    public String getJti(String token) {
        Claims claims = parser().parseClaimsJws(token).getBody();
        return claims.getId();
    }

    /**
     * 주어진 Refresh Token의 subject(userId) 반환
     */
    public Long getUserIdFromRefreshToken(String token) {
        Claims claims = parser().parseClaimsJws(token).getBody();
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 토큰에서 role 클레임 추출
     */
    public String getRole(String token) {
        Claims claims = parser().parseClaimsJws(token).getBody();
        return claims.get("role", String.class);
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

    // JWT 파서 공통 메서드
    private JwtParser parser() {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }
}
