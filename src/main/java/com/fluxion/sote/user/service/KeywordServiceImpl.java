package com.fluxion.sote.user.service;

import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.user.dto.KeywordResponse;
import com.fluxion.sote.user.entity.Keyword;
import com.fluxion.sote.user.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final KeywordRepository keywordRepo;

    @Override
    @Transactional(readOnly = true)
    public List<KeywordResponse> getKeywords() {
        var user = SecurityUtil.getCurrentUser();
        return keywordRepo.findByUser(user).stream()
                .map(k -> new KeywordResponse(k.getId(), k.getContent()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public KeywordResponse addKeyword(String content) {
        var user = SecurityUtil.getCurrentUser();
        if (keywordRepo.existsByUserAndContent(user, content)) {
            throw new IllegalArgumentException("이미 등록된 키워드입니다.");
        }
        Keyword saved = keywordRepo.save(new Keyword(content, user));
        return new KeywordResponse(saved.getId(), saved.getContent());
    }

    @Override
    @Transactional
    public void deleteKeyword(Long keywordId) {
        var user = SecurityUtil.getCurrentUser();
        Keyword keyword = keywordRepo.findByIdAndUser(keywordId, user)
                .orElseThrow(() -> new ResourceNotFoundException("키워드를 찾을 수 없습니다."));
        keywordRepo.delete(keyword);
    }
}
