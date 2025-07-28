package com.fluxion.sote.user.repository;

import com.fluxion.sote.user.entity.Keyword;
import com.fluxion.sote.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    // 현재 로그인한 사용자의 키워드 전체 조회
    List<Keyword> findByUser(User user);

    // 중복 키워드 방지를 위한 존재 여부 확인
    boolean existsByUserAndContent(User user, String content);
}
