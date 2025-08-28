package com.fluxion.sote.user.repository;

import com.fluxion.sote.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일 찾기용: 닉네임 + 생년월일로 사용자 조회
    Optional<User> findByNicknameAndBirthDate(String nickname, LocalDate birthDate);

    // 이메일로 사용자 조회 (비밀번호 찾기/중복 체크용)
    Optional<User> findByEmail(String email);
}
