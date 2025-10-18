package com.fluxion.sote.ocr.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.dto.OcrRequest;
import com.fluxion.sote.diary.service.DiaryService;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.user.repository.UserRepository;
import com.fluxion.sote.util.MultipartInputStreamFileResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 *  OCR 컨트롤러 (Spring ↔ FastAPI 연동)
 * - /api/ocr/preview : 사용자 → FastAPI OCR 호출 (JWT 필요)
 * - /api/ocr/results : FastAPI → Spring 일기 저장 (permitAll)
 * - /api/ocr/upload  :프론트 → Spring → FastAPI OCR 호출 (JWT 자동처리)
 */
@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
@Slf4j
public class OcrController {

    private final DiaryService diaryService;
    private final UserRepository userRepository;

    /**
     * FastAPI OCR 서버 엔드포인트
     * ex) http://localhost:8000/ocr/preview
     */
    @Value("${fastapi.ocr.url:http://localhost:8000/ocr/preview}")
    private String fastApiOcrUrl;

    // ====================================================
    //(1) 사용자 → FastAPI : OCR 미리보기 요청 (JWT 필요)
    // ====================================================
    @PostMapping("/preview")
    public ResponseEntity<Map<String, Object>> previewOcr(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) throws IOException {   // JWT 직접 받기

        User user = SecurityUtil.getCurrentUser();
        File tempFile = convertToFile(file);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", authHeader);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(tempFile));
        body.add("diary_date", java.time.LocalDate.now().toString());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> fastApiResponse = restTemplate.postForEntity(fastApiOcrUrl, requestEntity, Map.class);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("text", fastApiResponse.getBody().get("text"));
        response.put("imageUrl", fastApiResponse.getBody().get("imageUrl"));
        response.put("diaryDate", fastApiResponse.getBody().get("diaryDate"));
        response.put("status", "ok");

        return ResponseEntity.ok(response);
    }
    // ====================================================
    // (2) 신규: 프론트 → Spring → FastAPI OCR 프록시 (자동 uid)
    // ====================================================
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadOcr(
            @RequestParam("file") MultipartFile file) throws IOException {

        // 로그인된 사용자 ID 자동 추출
        User user = SecurityUtil.getCurrentUser();
        Long userId = user.getId();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // FastAPI로 전송할 FormData 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
        body.add("user_id", userId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // FastAPI로 OCR 요청
        ResponseEntity<Map> fastApiResponse = restTemplate.postForEntity(fastApiOcrUrl, requestEntity, Map.class);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("text", fastApiResponse.getBody().get("content"));
        response.put("imageUrl", fastApiResponse.getBody().get("imageUrl"));
        response.put("status", "ok");

        log.info("[OCR 자동화 완료] userId={}, file={}, textLength={}",
                userId, file.getOriginalFilename(), fastApiResponse.getBody().get("text") != null
                        ? fastApiResponse.getBody().get("text").toString().length()
                        : 0);

        return ResponseEntity.ok(response);
    }

    /**
     * MultipartFile → File 변환 (임시 저장)
     */
    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(convFile);
        return convFile;
    }

    // ====================================================
    //  FastAPI → Spring : OCR 결과 저장 (permitAll)
    // ====================================================
    @PostMapping("/results")
    public ResponseEntity<DiaryDto> saveOcrResult(@RequestBody OcrRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        DiaryDto dto = diaryService.writeOcr(
                user,
                request.getContent(),
                request.getImageUrl(),
                request.getDate(),
                request.getKeywordIds(),
                request.getEmotionType()
        );

        return ResponseEntity.ok(dto);
    }
}
