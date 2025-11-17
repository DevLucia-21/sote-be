package com.fluxion.sote.answer.service;

import com.fluxion.sote.answer.dto.QuestionAnswerDto;
import com.fluxion.sote.answer.entity.QuestionAnswer;
import com.fluxion.sote.answer.repository.QuestionAnswerRepository;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.question.entity.Question;
import com.fluxion.sote.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuestionAnswerServiceImpl implements QuestionAnswerService {

    private final QuestionRepository questionRepository;
    private final QuestionAnswerRepository answerRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");   // 날짜 계산용
    private static final Duration UPDATE_WINDOW = Duration.ofMinutes(10);
    private static final DateTimeFormatter YM_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private LocalDate toMonthFirstDay(YearMonth ym) {
        return ym.atDay(1);
    }

    private YearMonth parseOrNow(String yyyyMM) {
        return (yyyyMM == null || yyyyMM.isBlank())
                ? YearMonth.now(KST)
                : YearMonth.parse(yyyyMM, YM_FORMAT);
    }

    @Override
    public QuestionAnswerDto.Response create(User user, Long questionId, QuestionAnswerDto.CreateRequest req) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."));

        YearMonth ym = parseOrNow(req.getMonth());
        LocalDate monthFirst = toMonthFirstDay(ym);

        // 월별 중복 방지
        if (answerRepository.existsByUserIdAndQuestionIdAndAnswerMonth(user.getId(), q.getId(), monthFirst)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "해당 달에 이미 답변이 존재합니다.");
        }

        QuestionAnswer saved = answerRepository.save(
                QuestionAnswer.builder()
                        .user(user)
                        .question(q)
                        .answerText(req.getAnswerText())
                        .answeredAt(Instant.now())   // ⭐ UTC 기준 Instant
                        .answerMonth(monthFirst)
                        .build()
        );

        return toResponse(saved, q);
    }

    @Override
    public QuestionAnswerDto.Response update(User user, Long answerId, QuestionAnswerDto.UpdateRequest req) {
        log.info("[update] 요청 들어옴 :: answerId={}, currentUserId={}", answerId, user.getId());

        QuestionAnswer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "답변을 찾을 수 없습니다."));

        // ⭐ Instant로 통일
        Instant now = Instant.now();
        Duration elapsed = Duration.between(a.getAnsweredAt(), now);

        log.info("[Time Check] answerId={}, answeredAt={}, now={}, elapsed={}s (limit={}s)",
                a.getId(), a.getAnsweredAt(), now, elapsed.toSeconds(), UPDATE_WINDOW.getSeconds());

        if (!Objects.equals(a.getUser().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 답변만 수정할 수 있습니다.");
        }

        if (elapsed.compareTo(UPDATE_WINDOW) > 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성 후 10분이 지나 수정할 수 없습니다.");
        }

        a.setAnswerText(req.getAnswerText());
        a.setUpdatedAt(now);   // ⭐ UTC Instant

        QuestionAnswer saved = answerRepository.save(a);
        log.info("[update] 성공 :: answerId={}, userId={}", saved.getId(), user.getId());

        return toResponse(saved, saved.getQuestion());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionAnswerDto.MonthlyItem> getMyAnswersForMonth(User user, YearMonth ym) {
        LocalDate monthFirst = toMonthFirstDay(ym);

        return answerRepository
                .findAllWithQuestionByUserIdAndAnswerMonthOrderByQuestionDay(user.getId(), monthFirst)
                .stream()
                .map(a -> QuestionAnswerDto.MonthlyItem.builder()
                        .answerId(a.getId())
                        .questionId(a.getQuestion().getId())
                        .questionContent(a.getQuestion().getContent())
                        .questionDay(a.getQuestion().getId().intValue())
                        .answerText(a.getAnswerText())

                        // ⭐ 그대로 Instant 사용
                        .answeredAt(a.getAnsweredAt())
                        .updatedAt(a.getUpdatedAt())

                        // ⭐ 한국 날짜로 변환해야 함
                        .date(
                                a.getAnsweredAt()
                                        .atZone(KST)
                                        .toLocalDate()
                        )
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsForMeThisMonth(User user, Long questionId, YearMonth ym) {
        LocalDate monthFirst = toMonthFirstDay(ym);
        return answerRepository.existsByUserIdAndQuestionIdAndAnswerMonth(user.getId(), questionId, monthFirst);
    }

    private QuestionAnswerDto.Response toResponse(QuestionAnswer entity, Question q) {
        return QuestionAnswerDto.Response.builder()
                .id(entity.getId())
                .questionId(q.getId())
                .questionContent(q.getContent())
                .questionDay(q.getId().intValue())
                .answerText(entity.getAnswerText())
                .answeredAt(entity.getAnsweredAt())   // Instant 그대로 반환
                .answerMonth(entity.getAnswerMonth())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
