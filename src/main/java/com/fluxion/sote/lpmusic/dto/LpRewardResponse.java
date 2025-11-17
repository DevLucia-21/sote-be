package com.fluxion.sote.lpmusic.dto;

import com.fluxion.sote.lpmusic.entity.LpReward;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * LP 보상 응답 DTO
 * - Spotify 추천 정보 + 보상 지급 내역 포함
 */
@Builder
public record LpRewardResponse(
        Long id,
        String title,
        String artist,
        String album,
        String albumImageUrl,
        String playUrl,
        LocalDateTime recommendedAt,
        LocalDate rewardDate
) {
    public static LpRewardResponse fromEntity(LpReward r) {
        return LpRewardResponse.builder()
                .id(r.getId())
                .title(r.getTitle())
                .artist(r.getArtist())
                .album(r.getAlbum())
                .albumImageUrl(r.getAlbumImageUrl())
                .playUrl(r.getPlayUrl())
                .recommendedAt(r.getRecommendedAt())
                .rewardDate(r.getRewardDate())
                .build();
    }
}
