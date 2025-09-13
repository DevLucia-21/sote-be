package com.fluxion.sote.lpmusic.dto;

import com.fluxion.sote.lpmusic.entity.LpReward;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record LpRewardResponse(
        String title,
        String artist,
        String albumImageUrl,
        String playUrl,
        LocalDateTime recommendedAt,
        LocalDate rewardDate
) {
    public static LpRewardResponse fromEntity(LpReward r) {
        return LpRewardResponse.builder()
                .title(r.getTitle())
                .artist(r.getArtist())
                .albumImageUrl(r.getAlbumImageUrl())
                .playUrl(r.getPlayUrl())
                .recommendedAt(r.getRecommendedAt())
                .rewardDate(r.getRewardDate())
                .build();
    }
}
