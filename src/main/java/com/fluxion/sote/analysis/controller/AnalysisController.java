package com.fluxion.sote.analysis.controller;

import com.fluxion.sote.analysis.dto.AnalysisRequest;
import com.fluxion.sote.analysis.dto.AnalysisResponse;
import com.fluxion.sote.analysis.service.AnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService service;

    public AnalysisController(AnalysisService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AnalysisResponse> run(@Valid @RequestBody AnalysisRequest req) {
        AnalysisResponse res = service.run(req);

        // 기본 200, 에러 코드는 상황에 맞게 매핑
        HttpStatus status = HttpStatus.OK;
        if (!"ok".equalsIgnoreCase(res.getStatus())) {
            status = HttpStatus.BAD_GATEWAY; // AI 오류 기본값
            if (res.getData() != null) {
                Object code = res.getData().get("code");
                if ("ALREADY_ANALYZED_TODAY".equals(code)) {
                    status = HttpStatus.CONFLICT; // 409 (또는 HttpStatus.TOO_MANY_REQUESTS)
                }
            }
        }
        return ResponseEntity.status(status).body(res);
    }

    // 빠른 확인용(장르/텍스트 없이)
    @PostMapping("/simple")
    public ResponseEntity<AnalysisResponse> runSimple() {
        return run(new AnalysisRequest());
    }
}
