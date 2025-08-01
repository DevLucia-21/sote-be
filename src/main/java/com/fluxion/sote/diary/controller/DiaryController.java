// com.fluxion.sote.diary.controller.DiaryController.java
package com.fluxion.sote.diary.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.dto.DiaryDto;
import com.fluxion.sote.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public DiaryDto write(@RequestParam String content,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                          Principal principal) {
        User user = getUser(principal);
        return diaryService.write(user, content, date);
    }

    @PutMapping
    public DiaryDto update(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           @RequestParam String content,
                           Principal principal) {
        User user = getUser(principal);
        return diaryService.update(user, date, content);
    }

    @DeleteMapping
    public void delete(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       Principal principal) {
        User user = getUser(principal);
        diaryService.delete(user, date);
    }

    @GetMapping
    public List<DiaryDto> getByRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                     Principal principal) {
        User user = getUser(principal);
        return diaryService.getBetween(user, from, to);
    }

    @GetMapping("/day")
    public DiaryDto getOne(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           Principal principal) {
        User user = getUser(principal);
        return diaryService.getByDate(user, date);
    }

    private User getUser(Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }
}