// com.fluxion.sote.diary.service.DiaryService.java
package com.fluxion.sote.diary.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;

import java.time.LocalDate;
import java.util.List;

public interface DiaryService {
    DiaryDto write(User user, String content, LocalDate date);
    DiaryDto update(User user, LocalDate date, String newContent);
    void delete(User user, LocalDate date);
    DiaryDto getByDate(User user, LocalDate date);
    List<DiaryDto> getBetween(User user, LocalDate from, LocalDate to);
}