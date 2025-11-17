package com.fluxion.sote.diary.repository;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.user.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Optional<Diary> findByUserAndDate(User user, LocalDate date);

    List<Diary> findAllByUserAndDateBetween(User user, LocalDate from, LocalDate to);

    List<Diary> findAllByUserAndKeywordsContaining(User user, Keyword keyword);

    List<Diary> findByUser_IdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT d FROM Diary d JOIN d.keywords k " +
            "WHERE d.user = :user AND k.content LIKE %:keyword%")
    List<Diary> findByKeywordText(@Param("user") User user,
                                  @Param("keyword") String keyword);

    boolean existsByUserAndDate(User user, LocalDate date);

    /** OR 조건 (하나라도 포함) */
    @Query("""
        SELECT DISTINCT d FROM Diary d
        JOIN d.keywords k
        WHERE d.user = :user
          AND k.id IN :keywordIds
    """)
    List<Diary> findAllByUserAndKeywordsIn(@Param("user") User user,
                                           @Param("keywordIds") List<Long> keywordIds);

    /** AND 조건 (모두 포함) */
    @Query("""
        SELECT d FROM Diary d
        JOIN d.keywords k
        WHERE d.user = :user
          AND k.id IN :keywordIds
        GROUP BY d
        HAVING COUNT(DISTINCT k.id) = :keywordCount
    """)
    List<Diary> findAllByUserAndAllKeywords(@Param("user") User user,
                                            @Param("keywordIds") List<Long> keywordIds,
                                            @Param("keywordCount") long keywordCount);
}
