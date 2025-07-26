package com.fluxion.sote.musiclp.repository;

import com.fluxion.sote.musiclp.entity.MusicTrack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicTrackRepository extends JpaRepository<MusicTrack, Long> {
    // 기본 CRUD 메서드 제공
}
