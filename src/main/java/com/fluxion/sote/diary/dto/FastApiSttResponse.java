package com.fluxion.sote.diary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FastApiSttResponse {
    private String text;
    private String summary;
    private String emotionType;
    private Integer score;
    private String musicTitle;
    private String musicGenre;
}

