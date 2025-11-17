package com.fluxion.sote.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 기본 공통 오류
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // ----- 여기부터 워치 페어링 관련 커스텀 오류 -----

    INVALID_WATCH_PAIR_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 페어링 코드입니다."),
    EXPIRED_WATCH_PAIR_CODE(HttpStatus.BAD_REQUEST, "페어링 코드가 만료되었습니다."),
    WATCH_PAIR_CODE_RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "페어링 코드 시도 횟수가 초과되었습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
