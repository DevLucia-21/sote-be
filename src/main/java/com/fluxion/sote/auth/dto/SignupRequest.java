// src/main/java/com/fluxion/sote/auth/dto/SignupRequest.java
package com.fluxion.sote.auth.dto;

import com.fluxion.sote.global.enums.InstrumentType;
import com.fluxion.sote.global.validation.NoProfanity;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 회원가입 요청 DTO
 * - 보안 질문/답변은 user_security_answers 테이블에 별도 저장됨
 */
public record SignupRequest(
        @NotBlank @Email @Size(max = 30) String email,

        @NotBlank
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Pattern(
                regexp = "^(?=.*[^A-Za-z0-9]).+$",
                message = "비밀번호에는 특수문자가 최소 1개 포함되어야 합니다."
        )
        String password,

        @NotBlank @Size(max = 10) @NoProfanity
        String nickname,

        @NotNull @PastOrPresent
        LocalDate birthDate,

        @NotEmpty
        List<@NotNull Integer> musicPreferences,

        @NotBlank
        String securityAnswer,   // 보안 질문 답변 (BCrypt 저장)

        @NotNull
        Integer questionId,      // 보안 질문 ID (FK: security_questions.id)

        @NotNull
        InstrumentType character // 악기 캐릭터 선택
) {}
