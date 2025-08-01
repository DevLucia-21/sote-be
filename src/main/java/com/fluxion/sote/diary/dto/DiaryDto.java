package com.fluxion.sote.diary.dto;

import java.time.LocalDate;

public record DiaryDto(
        Long id,
        LocalDate date,
        String content
) {}