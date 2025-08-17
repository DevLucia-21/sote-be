package com.fluxion.sote.stt.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.exception.ForbiddenException;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.stt.entity.SttResult;
import com.fluxion.sote.stt.repository.SttResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class SttResultService {

    private final SttResultRepository sttResultRepository;

    @Transactional
    public Long saveSttResult(String text) {
        User user = SecurityUtil.getCurrentUser();

        // 오늘 0시 ~ 23:59:59
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        boolean exists = sttResultRepository.existsByUserIdAndCreatedAtBetween(
                user.getId(), startOfDay, endOfDay
        );

        if (exists) {
            throw new ForbiddenException("오늘은 이미 STT를 실행했습니다. 하루 1회만 가능합니다.");
        }

        SttResult result = new SttResult();
        result.setUserId(user.getId());
        result.setText(text);

        return sttResultRepository.save(result).getId();
    }

    @Transactional(readOnly = true)
    public SttResult getSttResult(Long id) {
        return sttResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("STT 결과를 찾을 수 없습니다."));
    }

    @Transactional
    public void updateSttResult(Long id, String newText) {
        SttResult result = getSttResult(id);
        result.updateText(newText);
        sttResultRepository.save(result);
    }

}
