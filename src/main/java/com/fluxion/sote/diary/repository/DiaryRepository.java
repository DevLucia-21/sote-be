// com.fluxion.sote.diary.repository.DiaryRepository.java
package com.fluxion.sote.diary.repository;

import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Optional<Diary> findByUserAndDate(User user, LocalDate date);
    List<Diary> findAllByUserAndDateBetween(User user, LocalDate from, LocalDate to);
}