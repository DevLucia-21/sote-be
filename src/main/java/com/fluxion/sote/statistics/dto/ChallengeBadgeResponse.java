package com.fluxion.sote.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChallengeBadgeResponse {
    private long badgeCount; // 누적 뱃지 개수
}
