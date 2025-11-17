package com.fluxion.sote.stt.service;

import com.fluxion.sote.stt.entity.SttResult;
import com.fluxion.sote.stt.repository.SttResultRepository;
import com.fluxion.sote.user.repository.UserRepository; // 추가
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * FastAPI 및 사용자용 STT 결과 서비스
 * - FastAPI 호출은 userId 기반으로 처리
 * - userId는 users 테이블의 FK이므로 존재 검증 수행
 */
@Service
@RequiredArgsConstructor
public class SttResultService {

    private final SttResultRepository sttResultRepository;
    private final UserRepository userRepository; // FK 검증용 추가

    //FastAPI → Spring 저장용

    @Transactional
    public Long saveSttResult(Long userId, String text) {

        //FK 무결성 검증: 존재하지 않는 userId 방지
        if (!userRepository.existsById(userId)) {
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        boolean exists = sttResultRepository.existsByUserIdAndCreatedAtBetween(
                userId, startOfDay, endOfDay
        );

        if (exists) {
            throw new IllegalStateException("오늘은 이미 STT를 실행했습니다. 하루 1회만 가능합니다.");
        }

        SttResult result = new SttResult();
        result.setUserId(userId);
        result.setText(text);

        return sttResultRepository.save(result).getId();
    }

    //STT 결과 조회

    @Transactional(readOnly = true)
    public SttResult getSttResult(Long id) {
        return sttResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("STT 결과를 찾을 수 없습니다."));
    }
    //STT 결과 수정
    @Transactional
    public void updateSttResult(Long id, String newText) {
        SttResult result = getSttResult(id);
        result.setText(newText);
        sttResultRepository.save(result);
    }
}
