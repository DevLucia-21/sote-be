package com.fluxion.sote.diary.repository;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Optional<Diary> findByUserAndDate(User user, LocalDate date);

    List<Diary> findAllByUserAndDateBetween(User user, LocalDate from, LocalDate to);

    // 키워드 기준으로 일기 조회
    @Query("SELECT d FROM Diary d JOIN d.keywords k " +
            "WHERE d.user = :user AND k.id = :keywordId")
    List<Diary> findByUserAndKeywordId(@Param("user") User user,
                                       @Param("keywordId") Long keywordId);
}
