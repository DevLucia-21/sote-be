// com.fluxion.sote.stress.repository.StressRecordRepository.java
package com.fluxion.sote.stress.repository;

import com.fluxion.sote.stress.entity.StressRecord;
import com.fluxion.sote.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StressRecordRepository extends JpaRepository<StressRecord, Long> {

    List<StressRecord> findAllByUserAndMeasuredAtBetween(User user, LocalDateTime start, LocalDateTime end);

    @Query("SELECT avg(s.hrv) FROM StressRecord s WHERE s.user = :user AND s.measuredAt BETWEEN :start AND :end")
    Double findAverageHrv(User user, LocalDateTime start, LocalDateTime end);
}
