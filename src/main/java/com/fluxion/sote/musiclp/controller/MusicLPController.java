package com.fluxion.sote.musiclp.controller;

import com.fluxion.sote.musiclp.dto.MusicLPDto;
import com.fluxion.sote.musiclp.service.MusicLPService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/music/lp")
public class MusicLPController {
    private final MusicLPService service;

    public MusicLPController(MusicLPService service) {
        this.service = service;
    }

    /** 주간 LP 목록 조회 */
    @GetMapping("/weekly")
    public ResponseEntity<List<MusicLPDto>> getWeeklyLPs() {
        return ResponseEntity.ok(service.getWeeklyLPs());
    }

    /** 월간 LP 목록 조회 */
    @GetMapping("/monthly")
    public ResponseEntity<List<MusicLPDto>> getMonthlyLPs() {
        return ResponseEntity.ok(service.getMonthlyLPs());
    }

    /** LP 상세 정보 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<MusicLPDto> getLPDetail(@PathVariable Long id) {
        return ResponseEntity.ok(service.getLPDetail(id));
    }
}
