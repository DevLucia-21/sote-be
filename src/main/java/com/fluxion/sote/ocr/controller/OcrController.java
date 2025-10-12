package com.fluxion.sote.ocr.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.dto.OcrRequest;
import com.fluxion.sote.diary.service.DiaryService;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * FastAPI → Spring : OCR 결과 저장 컨트롤러
 * - JWT 없이 permitAll (SecurityConfig/JwtFilter에서 예외 처리)
 * - userId를 본문에서 받아 User 엔티티를 로드해 DiaryService로 위임
 */
@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final DiaryService diaryService;
    private final UserRepository userRepository;

    @PostMapping("/results")
    public ResponseEntity<DiaryDto> saveOcrResult(@RequestBody OcrRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        DiaryDto dto = diaryService.writeOcr(
                user,
                request.getContent(),
                request.getImageUrl(),
                request.getDate(),
                request.getKeywordIds(),
                request.getEmotionType()
        );

        return ResponseEntity.ok(dto);
    }
}
