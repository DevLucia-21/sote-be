package com.fluxion.sote.diary.service.impl;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.dto.DiarySttResponse;
import com.fluxion.sote.diary.dto.FastApiSttResponse;
import com.fluxion.sote.diary.entity.WriteType;
import com.fluxion.sote.diary.service.DiaryService;
import com.fluxion.sote.diary.service.WatchDiaryService;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.util.MultipartInputStreamFileResource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class WatchDiaryServiceImpl implements WatchDiaryService {

    private final DiaryService diaryService;

    @Value("${fastapi.stt-url}")
    private String sttUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public DiarySttResponse writeSttDiary(MultipartFile file) {

        User user = SecurityUtil.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("로그인된 사용자 정보를 찾을 수 없습니다.");
        }

        // ★ FastAPI 호출 (file + user_id)
        FastApiSttResponse ai = callFastApiForStt(file, user.getId());

        // ★ 한국 날짜 기준
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // ★ content만 저장 (summary/score/music은 DB 저장 X — FastAPI 전용)
        DiaryDto diaryDto = diaryService.write(
                user,
                ai.getText(),    // STT 원문
                today,
                WriteType.STT,
                null,
                null
        );

        // ★ FastAPI 분석 결과 + DB 저장 결과 조합해서 워치로 응답
        return DiarySttResponse.builder()
                .diaryId(diaryDto.getId())
                .date(today.toString())
                .content(ai.getText())
                .summary(ai.getSummary())
                .emotionType(ai.getEmotionType())
                .analysisScore(ai.getScore())
                .musicTitle(ai.getMusicTitle())
                .musicGenre(ai.getMusicGenre())
                .build();
    }


    /**
     *  FastAPI STT 호출 (파일 + user_id)
     */
    private FastApiSttResponse callFastApiForStt(MultipartFile file, Long userId) {

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // 파일 전달
            body.add(
                    "file",
                    new MultipartInputStreamFileResource(
                            file.getInputStream(),
                            file.getOriginalFilename()
                    )
            );

            // ★ FastAPI가 반드시 요구하는 user_id
            body.add("user_id", userId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<FastApiSttResponse> response =
                    restTemplate.postForEntity(sttUrl, request, FastApiSttResponse.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("FastAPI STT 응답 실패: " + response.getStatusCode());
            }

            FastApiSttResponse result = response.getBody();
            if (result == null) {
                throw new RuntimeException("FastAPI 응답이 비어있습니다.");
            }

            if (result.getText() == null || result.getText().isBlank()) {
                throw new RuntimeException("STT 결과 text가 비어 있습니다.");
            }

            return result;

        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 실패", e);
        } catch (RestClientException e) {
            throw new RuntimeException("FastAPI STT 호출 실패", e);
        } catch (Exception e) {
            throw new RuntimeException("FastAPI STT 처리 중 오류 발생", e);
        }
    }
}
