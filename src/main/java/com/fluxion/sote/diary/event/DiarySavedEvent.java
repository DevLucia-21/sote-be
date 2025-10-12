package com.fluxion.sote.diary.event;

import com.fluxion.sote.diary.entity.Diary;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 일기 저장 완료 후 발행되는 이벤트
 */
@Getter
@AllArgsConstructor
public class DiarySavedEvent {
    private final Diary diary;
}
