package com.fluxion.sote.stress.controller;

import com.fluxion.sote.stress.dto.StressDto;
import com.fluxion.sote.stress.dto.StressUploadRequest;
import com.fluxion.sote.stress.service.StressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/watch/stress")
@RequiredArgsConstructor
public class StressController {

    private final StressService stressService;

    // 워치 → HRV/심박/걸음 업로드 (현재는 HRV만 저장)
    @PostMapping
    public ResponseEntity<StressDto> uploadStress(@RequestBody StressUploadRequest request) {
        return ResponseEntity.ok(
                stressService.saveStress(request.getHrv(), request.getMeasuredAt())
        );
    }

    // 오늘 평균 HRV 기반 스트레스 조회
    @GetMapping("/today")
    public ResponseEntity<StressDto> getTodayStress() {
        return ResponseEntity.ok(stressService.getTodayStress());
    }

    // 기간별 일 단위 평균 HRV 통계
    @GetMapping("/stats")
    public ResponseEntity<List<StressDto>> getStats(@RequestParam LocalDate from,
                                                    @RequestParam LocalDate to) {
        return ResponseEntity.ok(stressService.getStats(from, to));
    }
}
