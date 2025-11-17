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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuestionAnswerServiceImpl implements QuestionAnswerService {

    private final QuestionRepository questionRepository;
    private final QuestionAnswerRepository answerRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter YM_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private LocalDate toMonthFirstDay(YearMonth ym) {
        return ym.atDay(1);
    }

    private YearMonth parseOrNow(String yyyyMM) {
        return (yyyyMM == null || yyyyMM.isBlank())
                ? YearMonth.now(KST)
                : YearMonth.parse(yyyyMM, YM_FORMAT);
    }

    /**
     * 전시회 모드:
     * - 월에 여러 번 작성 가능
     * - 기존 답변이 있으면 덮어쓰기
     * - 수정 제한 없음
     */
    @Override
    public QuestionAnswerDto.Response create(User user, Long questionId, QuestionAnswerDto.CreateRequest req) {

        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."));

        YearMonth ym = parseOrNow(req.getMonth());
        LocalDate monthFirst = toMonthFirstDay(ym);

        // 🔥 전시회 모드 핵심: 기존 답변이 있으면 자동으로 덮어쓰기
        QuestionAnswer existing = answerRepository
                .findByUserIdAndQuestionIdAndAnswerMonth(user.getId(), q.getId(), monthFirst)
                .orElse(null);

        if (existing != null) {
            existing.setAnswerText(req.getAnswerText());
            existing.setUpdatedAt(Instant.now());  // 수정 시간 갱신
            QuestionAnswer saved = answerRepository.save(existing);
            return toResponse(saved, q);
        }

        // 기존 답변 없으면 새로 생성
        QuestionAnswer saved = answerRepository.save(
                QuestionAnswer.builder()
                        .user(user)
                        .question(q)
                        .answerText(req.getAnswerText())
                        .answeredAt(Instant.now())
                        .answerMonth(monthFirst)
                        .build()
        );

        return toResponse(saved, q);
    }

    /**
     * update는 사실상 필요없지만 유지.
     * 전시회 모드에서는 10분 제한 제거.
     */
    @Override
    public QuestionAnswerDto.Response update(User user, Long answerId, QuestionAnswerDto.UpdateRequest req) {

        QuestionAnswer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "답변을 찾을 수 없습니다."));

        if (!a.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 답변만 수정할 수 있습니다.");
        }

        // 🔥 10분 제한 제거: 언제든 수정 가능
        a.setAnswerText(req.getAnswerText());
        a.setUpdatedAt(Instant.now());

        QuestionAnswer saved = answerRepository.save(a);
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
                        .answeredAt(a.getAnsweredAt())
                        .updatedAt(a.getUpdatedAt())
                        .date(a.getAnsweredAt().atZone(KST).toLocalDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsForMeThisMonth(User user, Long questionId, YearMonth ym) {
        return false; // 전시회 모드: 항상 새로 작성 가능
    }

    private QuestionAnswerDto.Response toResponse(QuestionAnswer entity, Question q) {
        return QuestionAnswerDto.Response.builder()
                .id(entity.getId())
                .questionId(q.getId())
                .questionContent(q.getContent())
                .questionDay(q.getId().intValue())
                .answerText(entity.getAnswerText())
                .answeredAt(entity.getAnsweredAt())
                .answerMonth(entity.getAnswerMonth())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
