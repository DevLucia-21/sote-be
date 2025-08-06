package com.fluxion.sote.auth.dto;

import com.fluxion.sote.global.validation.NoProfanity;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record SignupRequest(
        @NotBlank @Email @Size(max = 30) String email,
        @NotBlank
        @Size(min = 8)
        @Pattern(
                regexp = "^(?=.*[^A-Za-z0-9]).+$"
        ) String password,
        @NotBlank @Size(max = 10) @NoProfanity String nickname,
        @NotNull @PastOrPresent LocalDate birthDate,
        @NotEmpty List<@NotNull Integer> musicPreferences,
        @NotBlank String securityAnswer,
        @NotNull Integer securityQuestionId,
        @NotBlank String character
) {}
