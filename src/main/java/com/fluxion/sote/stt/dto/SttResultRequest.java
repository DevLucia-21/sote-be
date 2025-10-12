package com.fluxion.sote.stt.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * FastAPI → Spring STT 결과 요청 DTO
 * - FastAPI가 userId와 text를 함께 전송
 */
@Getter
@Setter
public class SttResultRequest {

    private Long userId; //FastAPI가 전송하는 userId

    @Column(columnDefinition = "TEXT")
    private String text;
}
