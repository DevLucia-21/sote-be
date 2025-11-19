package com.fluxion.sote.lpmusic.repository;

import com.fluxion.sote.analysis.entity.AnalysisResult;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.lpmusic.entity.LpReward;
import com.fluxion.sote.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LpRewardRepository extends JpaRepository<LpReward, Long> {

    Optional<LpReward> findByUserAndRewardDate(User user, LocalDate rewardDate);

    boolean existsByUserAndRewardDate(User user, LocalDate rewardDate);

    List<LpReward> findAllByUserAndRewardDateBetweenOrderByRecommendedAtDesc(
            User user, LocalDate start, LocalDate end
    );

    List<LpReward> findAllByUserOrderByRecommendedAtDesc(User user);

    Optional<AnalysisResult> findTopByAnalysis_DiaryOrderByIdDesc(Diary diary);
}
