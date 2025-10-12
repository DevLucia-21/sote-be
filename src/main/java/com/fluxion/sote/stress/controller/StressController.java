// com.fluxion.sote.stress.controller.StressController.java
package com.fluxion.sote.stress.controller;

import com.fluxion.sote.stress.dto.StressDto;
import com.fluxion.sote.stress.service.StressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/watch/stress")
@RequiredArgsConstructor
public class StressController {

    private final StressService stressService;

    @PostMapping
    public ResponseEntity<StressDto> uploadStress(@RequestParam Double hrv,
                                                  @RequestParam LocalDateTime measuredAt) {
        return ResponseEntity.ok(stressService.saveStress(hrv, measuredAt));
    }

    @GetMapping("/today")
    public ResponseEntity<StressDto> getTodayStress() {
        return ResponseEntity.ok(stressService.getTodayStress());
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StressDto>> getStats(@RequestParam LocalDate from,
                                                    @RequestParam LocalDate to) {
        return ResponseEntity.ok(stressService.getStats(from, to));
    }
}
