// com/fluxion/sote/diary/controller/WatchDiaryController.java
package com.fluxion.sote.diary.controller;

import com.fluxion.sote.diary.dto.DiarySttResponse;
import com.fluxion.sote.diary.service.WatchDiaryService;
import com.fluxion.sote.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/watch/diary")
@RequiredArgsConstructor
public class WatchDiaryController {

    private final WatchDiaryService watchDiaryService;

    @PostMapping("/stt")
    public ResponseEntity<DiarySttResponse> uploadSttDiary(
            @RequestPart("file") MultipartFile file) {

        System.out.println("🔥 /api/watch/diary/stt 진입");
        System.out.println("🔥 현재 사용자 = " + SecurityUtil.getCurrentUserId());

        DiarySttResponse response = watchDiaryService.writeSttDiary(file);
        return ResponseEntity.ok(response);
    }
}