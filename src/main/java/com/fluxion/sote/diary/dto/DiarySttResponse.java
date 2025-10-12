// com/fluxion/sote/diary/dto/DiarySttResponse.java
package com.fluxion.sote.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiarySttResponse {
    private Long diaryId;
    private String date;
    private String content;
}
