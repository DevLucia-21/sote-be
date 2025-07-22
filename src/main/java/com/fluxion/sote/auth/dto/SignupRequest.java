package com.fluxion.sote.auth.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record SignupRequest(
        @NotBlank @Email @Size(max = 30) String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank @Size(max = 10) String nickname,
        @NotNull @PastOrPresent LocalDate birthDate,
        @NotEmpty List<@NotNull Integer> musicPreferences,
        @NotBlank String securityAnswer
) {}
