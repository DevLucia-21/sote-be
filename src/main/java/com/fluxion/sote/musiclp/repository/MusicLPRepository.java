package com.fluxion.sote.musiclp.repository;

import com.fluxion.sote.musiclp.entity.MusicLP;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MusicLPRepository extends JpaRepository<MusicLP, Long> {
    /**
     * releaseDate가 start와 end 사이인 LP들을 조회합니다.
     * 주간·월간 조회 모두 이 메서드를 사용하세요.
     */
    List<MusicLP> findByReleaseDateBetween(LocalDate start, LocalDate end);
}