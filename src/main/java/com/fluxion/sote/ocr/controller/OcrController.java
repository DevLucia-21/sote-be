package com.fluxion.sote.ocr.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.dto.OcrRequest;
import com.fluxion.sote.diary.service.DiaryService;
import com.fluxion.sote.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final DiaryService diaryService;

    private User getCurrentUser() {
        return SecurityUtil.getCurrentUser();
    }

    @PostMapping("/results")
    public ResponseEntity<DiaryDto> saveOcrResult(@RequestBody OcrRequest request) {
        User user = getCurrentUser();
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
