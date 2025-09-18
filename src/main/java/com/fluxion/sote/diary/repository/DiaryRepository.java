package com.fluxion.sote.diary.repository;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.user.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Optional<Diary> findByUserAndDate(User user, LocalDate date);

    List<Diary> findAllByUserAndDateBetween(User user, LocalDate from, LocalDate to);

    // ✅ 키워드 기준 일기 조회 (User+Keyword 객체로 보장)
    List<Diary> findAllByUserAndKeywordsContaining(User user, Keyword keyword);

    // 특정 사용자(userId)의 지정된 월(start~end) 일기 조회
    List<Diary> findByUser_IdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    boolean existsByUserAndDate(User user, LocalDate date);
}
