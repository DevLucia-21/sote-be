// com/fluxion/sote/diary/service/WatchDiaryService.java
package com.fluxion.sote.diary.service;

import com.fluxion.sote.diary.dto.DiarySttResponse;
import org.springframework.web.multipart.MultipartFile;

public interface WatchDiaryService {
    DiarySttResponse writeSttDiary(MultipartFile file);
}
