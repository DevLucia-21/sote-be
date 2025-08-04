package com.fluxion.sote.diary.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.repository.AuthRepository;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;
    private final AuthRepository userRepository;

    @PostMapping
    public DiaryDto write(@RequestBody DiaryDto.CreateRequest request) {
        User user = getCurrentUser();
        return diaryService.write(user, request.getContent(), request.getDate());
    }

    @PutMapping
    public DiaryDto update(@RequestBody DiaryDto.UpdateRequest request) {
        User user = getCurrentUser();
        return diaryService.update(user, request.getDate(), request.getContent());
    }

    @DeleteMapping
    public void delete(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        User user = getCurrentUser();
        diaryService.delete(user, date);
    }

    @GetMapping
    public List<DiaryDto> getByRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        User user = getCurrentUser();
        return diaryService.getBetween(user, from, to);
    }

    @GetMapping("/day")
    public DiaryDto getOne(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        User user = getCurrentUser();
        return diaryService.getByDate(user, date);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("현재 인증된 사용자가 없습니다.");
        }

        Object principal = auth.getPrincipal();
        System.out.println("[DEBUG] SecurityContext principal = " + principal); //test


        if (principal instanceof Long userId) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> {
                        System.out.println("[DEBUG] 해당 userId로 사용자를 찾을 수 없습니다.");//test
                        return new IllegalStateException("로그인된 사용자를 찾을 수 없습니다.");
                    });
        }

        // principal이 String인데 "anonymousUser"이면 비로그인 사용자임
        if (principal instanceof String s && s.equals("anonymousUser")) {
            System.out.println("[DEBUG] anonymousUser 감지됨."); //test
            throw new IllegalStateException("로그인되지 않은 사용자입니다.");
        }

        // 예외 케이스 대비
        System.out.println("[DEBUG] 예상치 못한 principal 형식: " + principal);
        throw new IllegalStateException("알 수 없는 인증 정보입니다: " + principal);

    }

}