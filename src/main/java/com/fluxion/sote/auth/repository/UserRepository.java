package com.fluxion.sote.auth.repository;

import com.fluxion.sote.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인용: 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 회원가입 중복 체크용: 이메일 존재 여부
    boolean existsByEmail(String email);

    // 이메일 찾기용: 닉네임(nickname)과 보안답변(securityAnswer)으로 사용자 조회
    Optional<User> findByNicknameAndSecurityAnswer(String nickname, String securityAnswer);

    // 비밀번호 찾기용: 이메일 + 보안답변으로 사용자 조회
    Optional<User> findByEmailAndSecurityAnswer(String email, String securityAnswer);
}
