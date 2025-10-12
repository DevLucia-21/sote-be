// com.fluxion.sote.stress.service.StressService.java
package com.fluxion.sote.stress.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.stress.dto.StressDto;
import com.fluxion.sote.stress.entity.StressLevel;
import com.fluxion.sote.stress.entity.StressRecord;
import com.fluxion.sote.stress.repository.StressRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StressService {

    private final StressRecordRepository stressRepo;

    public StressDto saveStress(Double hrv, LocalDateTime measuredAt) {
        User user = SecurityUtil.getCurrentUser();
        StressLevel level = StressLevel.fromHrv(hrv);

        StressRecord record = StressRecord.builder()
                .user(user)
                .hrv(hrv)
                .stressLevel(level)
                .measuredAt(measuredAt)
                .createdAt(LocalDateTime.now())
                .build();

        stressRepo.save(record);

        return StressDto.builder()
                .id(record.getId())
                .hrv(hrv)
                .stressLevel(level)
                .measuredAt(measuredAt)
                .build();
    }

    public StressDto getTodayStress() {
        User user = SecurityUtil.getCurrentUser();
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        Double avgHrv = stressRepo.findAverageHrv(user, start, end);
        StressLevel level = StressLevel.fromHrv(avgHrv);

        return StressDto.builder()
                .averageHrv(avgHrv)
                .stressLevel(level)
                .date(today.toString())
                .build();
    }

    public List<StressDto> getStats(LocalDate from, LocalDate to) {
        User user = SecurityUtil.getCurrentUser();
        return from.datesUntil(to.plusDays(1))
                .map(date -> {
                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = date.atTime(LocalTime.MAX);
                    Double avgHrv = stressRepo.findAverageHrv(user, start, end);
                    return StressDto.builder()
                            .date(date.toString())
                            .averageHrv(avgHrv)
                            .stressLevel(StressLevel.fromHrv(avgHrv))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
