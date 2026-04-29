package com.fluxion.sote.stt.service;

import com.fluxion.sote.stt.entity.SttResult;
import com.fluxion.sote.stt.repository.SttResultRepository;
import com.fluxion.sote.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SttResultService {

    private final SttResultRepository sttResultRepository;
    private final UserRepository userRepository;

    /**
     * STT 결과 저장
     * - FastAPI에서 변환된 STT 텍스트를 Spring DB에 저장한다.
     * - 기존 전시회용 로직처럼 오늘 결과를 덮어쓰지 않는다.
     * - 호출 결과마다 새로운 STT 결과로 저장한다.
     *
     * 하루 1회 제한은 FastAPI Redis에서 처리한다.
     */
    @Transactional
    public Long saveSttResult(Long userId, String text) {

        if (userId == null) {
            throw new IllegalArgumentException("userId가 필요합니다.");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId);
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("STT 결과 텍스트가 비어 있습니다.");
        }

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
        if (newText == null || newText.isBlank()) {
            throw new IllegalArgumentException("수정할 STT 텍스트가 비어 있습니다.");
        }

        SttResult result = getSttResult(id);
        result.setText(newText);

        sttResultRepository.save(result);
    }
}