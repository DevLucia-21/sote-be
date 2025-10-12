package com.fluxion.sote.diary.service.impl;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.dto.DiarySttResponse;
import com.fluxion.sote.diary.entity.WriteType;
import com.fluxion.sote.diary.service.DiaryService;
import com.fluxion.sote.diary.service.WatchDiaryService;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.util.MultipartInputStreamFileResource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WatchDiaryServiceImpl implements WatchDiaryService {

    private final DiaryService diaryService;   // 공통 서비스 사용

    @Override
    public DiarySttResponse writeSttDiary(MultipartFile file) {
        // 1. 현재 로그인한 사용자 조회
        User user = SecurityUtil.getCurrentUser();

        // 2. FastAPI 호출 → text 추출
        String content = callFastApiForStt(file);

        // 3. 오늘 날짜
        LocalDate today = LocalDate.now();

        // 4. Diary 저장 (DiaryService 공통 로직 이용 → 자동 감정분석까지 실행됨)
        DiaryDto diaryDto = diaryService.write(
                user,
                content,
                today,
                WriteType.STT,
                null,   // STT에서는 키워드 없음
                null    // 감정은 자동분석 결과로 반영됨
        );

        // 5. 결과 응답
        return DiarySttResponse.builder()
                .diaryId(diaryDto.getId())
                .date(today.toString())
                .content(diaryDto.getContent())
                .build();
    }

    private String callFastApiForStt(MultipartFile file) {
        try {
            String fastApiUrl = "http://localhost:8000/ai/stt/transcribe";
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, requestEntity, Map.class);
            Map<String, Object> result = response.getBody();

            if (result != null && result.containsKey("text")) {
                return result.get("text").toString();
            }
            throw new RuntimeException("STT 응답에 text 필드 없음");
        } catch (Exception e) {
            throw new RuntimeException("STT 변환 요청 실패", e);
        }
    }
}
