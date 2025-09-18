package com.fluxion.sote.user.repository;

import com.fluxion.sote.user.entity.Keyword;
import com.fluxion.sote.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    // 현재 로그인한 사용자의 키워드 전체 조회
    List<Keyword> findByUser(User user);

    // 중복 키워드 방지를 위한 존재 여부 확인
    boolean existsByUserAndContent(User user, String content);

    // 특정 사용자의 특정 키워드 삭제
    @Modifying
    @Transactional
    void deleteByIdAndUser(Long id, User user);

    // 사용자 소유 키워드만 단일 조회
    Optional<Keyword> findByIdAndUser(Long id, User user);

    // 여러 키워드도 사용자 기준으로만 조회
    List<Keyword> findAllByIdInAndUser(List<Long> ids, User user);
}
