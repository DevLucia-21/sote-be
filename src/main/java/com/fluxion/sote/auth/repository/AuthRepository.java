package com.fluxion.sote.auth.repository;

import com.fluxion.sote.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<User, Long> {

    // 로그인용: 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 회원가입 중복 체크용: 이메일 존재 여부
    boolean existsByEmail(String email);
}
