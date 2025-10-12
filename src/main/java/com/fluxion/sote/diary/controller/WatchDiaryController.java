// com/fluxion/sote/diary/controller/WatchDiaryController.java
package com.fluxion.sote.diary.controller;

import com.fluxion.sote.diary.dto.DiarySttResponse;
import com.fluxion.sote.diary.service.WatchDiaryService;
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
    public ResponseEntity<DiarySttResponse> writeSttDiary(
            @RequestPart MultipartFile file
    ) {
        DiarySttResponse response = watchDiaryService.writeSttDiary(file);
        return ResponseEntity.ok(response);
    }
}
