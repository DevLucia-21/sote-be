package com.fluxion.sote.user.controller;

import com.fluxion.sote.user.dto.KeywordCreateRequest;
import com.fluxion.sote.user.dto.KeywordResponse;
import com.fluxion.sote.user.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    /**
     * 키워드 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<KeywordResponse>> getKeywords() {
        return ResponseEntity.ok(keywordService.getKeywords());
    }

    /**
     * 키워드 등록
     */
    @PostMapping
    public ResponseEntity<KeywordResponse> addKeyword(@RequestBody KeywordCreateRequest request) {
        KeywordResponse response = keywordService.addKeyword(request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 키워드 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable("id") Long id) {
        keywordService.deleteKeyword(id);
        return ResponseEntity.noContent().build();
    }
}
