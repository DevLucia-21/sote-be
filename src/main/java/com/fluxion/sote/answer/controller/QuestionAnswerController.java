package com.fluxion.sote.answer.controller;

import com.fluxion.sote.answer.dto.QuestionAnswerDto;
import com.fluxion.sote.answer.service.QuestionAnswerService;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionAnswerController {
    private final QuestionAnswerService answerService;
    private final AuthRepository authRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = (auth != null) ? auth.getName() : null;
        if (name == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        if (name.matches("\\d+")) {
            Long userId = Long.parseLong(name);
            return authRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
        } else {
            return authRepository.findByEmail(name)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
        }
    }

    /** 작성 */
    @PostMapping("/{questionId}/answers")
    public QuestionAnswerDto.Response create(@PathVariable Long questionId,
                                             @RequestBody QuestionAnswerDto.CreateRequest req) {
        User me = getCurrentUser();
        return answerService.create(me, questionId, req);
    }

    /** 수정 (10분 이내만 가능) */
    @PutMapping("/answers/{answerId}")
    public QuestionAnswerDto.Response update(@PathVariable Long answerId,
                                             @RequestBody QuestionAnswerDto.UpdateRequest req) {
        System.out.println("🚀 Controller update() reached :: answerId=" + answerId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("DEBUG >>> Controller update 호출됨, auth=" + auth);
        System.out.println("DEBUG >>> auth.getName()=" + (auth != null ? auth.getName() : null));
        User me = getCurrentUser();
        System.out.println("DEBUG >>> Controller update 호출됨, answerId=" + answerId + ", currentUserId=" + me.getId());
        return answerService.update(me, answerId, req);
    }

    /** 월별 내 답변(질문 본문 포함) */
    @GetMapping("/answers/me")
    public List<QuestionAnswerDto.MonthlyItem> myMonthlyAnswers(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        YearMonth target = (month == null)
                ? YearMonth.parse(java.time.ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM")))
                : month;
        User me = getCurrentUser();
        return answerService.getMyAnswersForMonth(me, target);
    }

    /** 특정 질문에 대해 해당 월에 내가 작성했는지 여부 체크 */
    @GetMapping("/{questionId}/answers/me/exist")
    public boolean existsForMeThisMonth(
            @PathVariable Long questionId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
    ) {
        YearMonth target = (month == null)
                ? YearMonth.parse(java.time.ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM")))
                : month;
        User me = getCurrentUser();
        return answerService.existsForMeThisMonth(me, questionId, target);
    }
}
