package com.fluxion.sote.stt.service;

import com.fluxion.sote.stt.entity.SttResult;
import com.fluxion.sote.stt.repository.SttResultRepository;
import com.fluxion.sote.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    /**
     * 전시회용 STT 저장:
     * - 하루 여러 번 호출 가능
     * - 오늘 기록이 있으면 최신 것 하나 찾아서 text 덮어쓰기
     */
    @Transactional
    public Long saveSttResult(Long userId, String text) {

        // FK 검증 (그냥 방어용)
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId);
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        //오늘 기준 STT 결과 중 가장 최근 것 조회
        SttResult existing = sttResultRepository
                .findTopByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                        userId, startOfDay, endOfDay
                )
                .orElse(null);

        if (existing != null) {
            // 이미 오늘 STT가 있으면 덮어쓰기
            existing.setText(text);
            return sttResultRepository.save(existing).getId();
        }

        // 오늘 기록이 없으면 새로 생성
        SttResult result = new SttResult();
        result.setUserId(userId);
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
        result.setText(newText);
        sttResultRepository.save(result);
    }
}
