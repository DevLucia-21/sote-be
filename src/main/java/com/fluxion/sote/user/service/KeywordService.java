// src/main/java/com/fluxion/sote/user/service/KeywordService.java
package com.fluxion.sote.user.service;

import com.fluxion.sote.user.dto.KeywordResponse;

import java.util.List;

public interface KeywordService {
    /**
     * 현재 로그인한 사용자의 키워드 전체 조회
     */
    List<KeywordResponse> getKeywords();

    /**
     * 키워드 등록
     * @param content 키워드 내용
     * @return 생성된 키워드 정보
     */
    KeywordResponse addKeyword(String content);

    /**
     * 키워드 삭제
     * @param keywordId 삭제할 키워드 ID
     */
    void deleteKeyword(Long keywordId);
}
