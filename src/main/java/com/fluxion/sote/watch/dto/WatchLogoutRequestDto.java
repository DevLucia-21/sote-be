// src/main/java/com/fluxion/sote/watch/dto/request/WatchLogoutRequestDto.java
package com.fluxion.sote.watch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class WatchLogoutRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Logout {
        // 워치를 구분하는 ID가 있다면 사용 (없으면 null로 보내도 됨)
        private String deviceId;
    }
}
