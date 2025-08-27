package com.fluxion.sote.musiclp.controller;

import com.fluxion.sote.musiclp.dto.PlayUrlDto;
import com.fluxion.sote.musiclp.service.MusicTrackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/music")
public class MusicPlayController {
    private final MusicTrackService trackService;

    public MusicPlayController(MusicTrackService trackService) {
        this.trackService = trackService;
    }

    /** 재생 URL 반환 */
    @GetMapping("/play/{trackId}")
    public ResponseEntity<PlayUrlDto> getPlayUrl(@PathVariable Long trackId) {
        String url = trackService.getPlayUrl(trackId);
        return ResponseEntity.ok(new PlayUrlDto(url));
    }
}
