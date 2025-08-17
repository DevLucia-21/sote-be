package com.fluxion.sote.stt.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SttResultRequest {

    @Column(columnDefinition = "TEXT")
    private String text;
}
