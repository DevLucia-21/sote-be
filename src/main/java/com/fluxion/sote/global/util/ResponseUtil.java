// src/main/java/com/fluxion/sote/global/util/ResponseUtil.java
package com.fluxion.sote.global.util;

import org.springframework.http.ResponseEntity;

/**
 * 컨트롤러의 공통 ResponseEntity 빌더를 모아둔 헬퍼 클래스
 */
public final class ResponseUtil {
    private ResponseUtil() {}

    /** 201 Created, Body 없이 Location 헤더만 설정하고 싶을 때 */
    public static <T> ResponseEntity<T> created() {
        return ResponseEntity.status(201).build();
    }

    /** 204 No Content */
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /** 200 OK, Body 포함 */
    public static <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }

    /** 200 OK, Body 없이 */
    public static ResponseEntity<Void> ok() {
        return ResponseEntity.ok().build();
    }
}
