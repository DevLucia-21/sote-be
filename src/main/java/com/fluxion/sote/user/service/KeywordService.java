package com.fluxion.sote.user.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.user.dto.KeywordResponse;
import com.fluxion.sote.user.entity.Keyword;
import com.fluxion.sote.user.repository.KeywordRepository;
import com.fluxion.sote.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KeywordService {

    private final KeywordRepository keywordRepo;
    private final UserRepository userRepo;

    public KeywordService(KeywordRepository keywordRepo, UserRepository userRepo) {
        this.keywordRepo = keywordRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<KeywordResponse> getKeywords() {
        User user = getCurrentUser();
        return keywordRepo.findByUser(user).stream()
                .map(k -> new KeywordResponse(k.getId(), k.getContent()))
                .toList();
    }

    @Transactional
    public void addKeyword(String content) {
        User user = getCurrentUser();

        if (keywordRepo.existsByUserAndContent(user, content)) {
            throw new IllegalArgumentException("이미 등록된 키워드입니다.");
        }

        Keyword keyword = new Keyword(content, user);
        keywordRepo.save(keyword);
    }

    @Transactional
    public void deleteKeyword(Long keywordId) {
        User user = getCurrentUser();
        Keyword keyword = keywordRepo.findById(keywordId)
                .orElseThrow(() -> new ResourceNotFoundException("키워드를 찾을 수 없습니다."));

        if (!keyword.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("자신의 키워드만 삭제할 수 있습니다.");
        }

        keywordRepo.delete(keyword);
    }

    private User getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
