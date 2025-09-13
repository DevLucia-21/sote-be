package com.fluxion.sote.analysis.repository;

import com.fluxion.sote.analysis.entity.AnalysisResult;
import com.fluxion.sote.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    Optional<AnalysisResult> findByAnalysis_Id(Long analysisId);

    /**
     * 최근 3일(오늘 제외: 어제/그제/그끄제) 동안 선택된 곡의 (title||artist) 키 목록을 소문자로 반환.
     * KST 기준 날짜 사용. DATE 연산으로 타입 혼합을 피한다.
     */
    @Query(value = """
        SELECT concat(lower(ar.selected_track_title), '||', lower(ar.selected_track_artist))
        FROM analysis_result ar
        JOIN analysis a ON a.id = ar.analysis_id
        WHERE a.user_id = :userId
          AND a.analysis_date >= ((now() AT TIME ZONE 'Asia/Seoul')::date - 3)
          AND a.analysis_date  <  ((now() AT TIME ZONE 'Asia/Seoul')::date)   -- 오늘 제외
          AND ar.selected_track_title IS NOT NULL
          AND ar.selected_track_artist IS NOT NULL
        """, nativeQuery = true)
    List<String> findRecentSelectedKeys(@Param("userId") Long userId);

    // 특정 유저의 최신 분석 결과
    Optional<AnalysisResult> findTopByAnalysis_User_IdOrderByCreatedAtDesc(Long userId);

    //특정일기에 연결된 분석 결과 조회
    Optional<AnalysisResult> findByAnalysis_Diary(Diary diary);
}
