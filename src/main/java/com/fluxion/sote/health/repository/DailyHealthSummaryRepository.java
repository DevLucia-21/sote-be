package com.fluxion.sote.health.repository;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.health.entity.DailyHealthSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyHealthSummaryRepository
        extends JpaRepository<DailyHealthSummary, Long> {

    Optional<DailyHealthSummary> findByUserAndDate(User user, LocalDate date);

    List<DailyHealthSummary> findAllByUserAndDateBetween(
            User user,
            LocalDate from,
            LocalDate to
    );
}
