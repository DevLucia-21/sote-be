package com.fluxion.sote.global.util;

/**
 * JWT 관련 Redis 키 프리픽스 등을 한곳에 모아둔 상수 클래스입니다.
 */
public final class JwtConstants {

    /** Redis에 저장할 리프레시 토큰 JTI 프리픽스 */
    public static final String REFRESH_PREFIX = "refresh:";

    /** 로그아웃 시 블랙리스트에 저장할 JTI 프리픽스 */
    public static final String BLACKLIST_PREFIX = "blacklist:";

    // 인스턴스화 방지
    private JwtConstants() {}
}
