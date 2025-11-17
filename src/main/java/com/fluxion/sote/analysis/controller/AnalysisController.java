package com.fluxion.sote.analysis.controller;

import com.fluxion.sote.analysis.dto.AnalysisRequest;
import com.fluxion.sote.analysis.dto.AnalysisResponse;
import com.fluxion.sote.analysis.entity.Analysis;
import com.fluxion.sote.analysis.entity.AnalysisResult;
import com.fluxion.sote.analysis.repository.AnalysisRepository;
import com.fluxion.sote.analysis.repository.AnalysisResultRepository;
import com.fluxion.sote.analysis.service.AnalysisService;
import com.fluxion.sote.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService service;
    private final AnalysisRepository analysisRepo;
    private final AnalysisResultRepository resultRepo;

    /**
     * 감정분석 실행
     * - 오늘 일기: 감정 + 음악 + 챌린지·LP 지급
     * - 과거 일기: 감정 + 음악만 (챌린지·LP 제외)
     */
    @PostMapping
    public ResponseEntity<AnalysisResponse> run(@Valid @RequestBody AnalysisRequest req) {
        AnalysisResponse res = service.run(req);

        HttpStatus status = HttpStatus.OK;
        if (!"ok".equalsIgnoreCase(res.getStatus())) {
            status = HttpStatus.BAD_GATEWAY;
            if (res.getData() != null) {
                Object code = res.getData().get("code");
                if ("ALREADY_ANALYZED_TODAY".equals(code)) {
                    status = HttpStatus.CONFLICT;
                }
            }
        }
        return ResponseEntity.status(status).body(res);
    }

    /** 빠른 확인용 (디버그용 단순 감정 분석) */
    @PostMapping("/simple")
    public ResponseEntity<AnalysisResponse> runSimple() {
        return run(new AnalysisRequest());
    }

    /**
     * 분석 결과 조회
     * - 일기 ID 기준으로 감정·음악 결과 반환
     */
    @GetMapping("/{diaryId}")
    public ResponseEntity<?> getAnalysis(@PathVariable Long diaryId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Analysis analysis = analysisRepo.findByUserIdAndDiaryId(userId, diaryId).orElse(null);
        if (analysis == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "분석 기록이 없습니다."));
        }

        AnalysisResult result = analysis.getResult();
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("message", "아직 분석 결과가 없습니다."));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("analysisDate", analysis.getAnalysisDate());
        body.put("emotionLabel", result.getEmotionLabel());
        body.put("emotionScore", result.getEmotionScore());
        body.put("emotionReason", result.getEmotionReason());
        body.put("selectedTrackTitle", result.getSelectedTrackTitle());
        body.put("selectedTrackArtist", result.getSelectedTrackArtist());
        body.put("selectedTrackAlbum", result.getSelectedTrackAlbum());
        body.put("selectedTrackGenre", result.getSelectedTrackGenre());
        body.put("selectedTrackReason", result.getSelectedTrackReason());
        body.put("selectedTrackCoverImageUrl", result.getSelectedTrackCoverImageUrl());

        return ResponseEntity.ok(body);

    }
}
