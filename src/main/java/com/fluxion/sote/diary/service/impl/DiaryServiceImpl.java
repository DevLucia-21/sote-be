package com.fluxion.sote.diary.service.impl;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.diary.repository.DiaryRepository;
import com.fluxion.sote.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepo;

    @Override
    @Transactional
    public DiaryDto write(User user, String content, LocalDate date) {
        diaryRepo.findByUserAndDate(user, date).ifPresent(d -> {
            throw new IllegalArgumentException("이미 작성한 일기가 있습니다.");
        });

        Diary diary = Diary.builder()
                .user(user)
                .date(date)
                .content(content)
                .build();
        diaryRepo.save(diary);

        return new DiaryDto(diary.getId(), diary.getDate(), diary.getContent());
    }

    @Override
    @Transactional
    public DiaryDto update(User user, LocalDate date, String content) {
        Diary diary = diaryRepo.findByUserAndDate(user, date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 일기가 존재하지 않습니다."));
        diary.setContent(content);
        return new DiaryDto(diary.getId(), diary.getDate(), diary.getContent());
    }


    @Override
    @Transactional
    public void delete(User user, LocalDate date) {
        Diary diary = diaryRepo.findByUserAndDate(user, date)
                .orElseThrow(() -> new IllegalArgumentException("일기가 없습니다."));
        diaryRepo.delete(diary);
    }

    @Override
    @Transactional(readOnly = true)
    public DiaryDto getByDate(User user, LocalDate date) {
        Diary diary = diaryRepo.findByUserAndDate(user, date)
                .orElseThrow(() -> new IllegalArgumentException("일기가 없습니다."));
        return new DiaryDto(diary.getId(), diary.getDate(), diary.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiaryDto> getBetween(User user, LocalDate from, LocalDate to) {
        return diaryRepo.findAllByUserAndDateBetween(user, from, to)
                .stream()
                .map(d -> new DiaryDto(d.getId(), d.getDate(), d.getContent()))
                .toList();
    }
}