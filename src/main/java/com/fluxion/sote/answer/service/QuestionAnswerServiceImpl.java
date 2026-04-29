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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
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
    private static final Duration EDIT_LIMIT = Duration.ofMinutes(10);

    private LocalDate toMonthFirstDay(YearMonth ym) {
        return ym.atDay(1);
    }

    private YearMonth parseOrNow(String yyyyMM) {
        return (yyyyMM == null || yyyyMM.isBlank())
                ? YearMonth.now(KST)
                : YearMonth.parse(yyyyMM, YM_FORMAT);
    }

    /**
     * 질문 답변 작성
     * - 같은 사용자는 같은 월에 같은 질문에 대해 한 번만 답변 가능
     * - 기존 답변이 있으면 덮어쓰지 않고 409 Conflict 반환
     */
    @Override
    public QuestionAnswerDto.Response create(User user, Long questionId, QuestionAnswerDto.CreateRequest req) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."));

        YearMonth ym = parseOrNow(req.getMonth());
        LocalDate monthFirst = toMonthFirstDay(ym);

        boolean alreadyExists = answerRepository
                .findByUserIdAndQuestionIdAndAnswerMonth(user.getId(), question.getId(), monthFirst)
                .isPresent();

        if (alreadyExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 이번 달에 해당 질문에 답변했습니다."
            );
        }

        QuestionAnswer saved = answerRepository.save(
                QuestionAnswer.builder()
                        .user(user)
                        .question(question)
                        .answerText(req.getAnswerText())
                        .answeredAt(Instant.now())
                        .answerMonth(monthFirst)
                        .build()
        );

        return toResponse(saved, question);
    }

    /**
     * 질문 답변 수정
     * - 본인 답변만 수정 가능
     * - 작성 후 10분 이내에만 수정 가능
     */
    @Override
    public QuestionAnswerDto.Response update(User user, Long answerId, QuestionAnswerDto.UpdateRequest req) {

        QuestionAnswer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "답변을 찾을 수 없습니다."));

        if (!answer.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 답변만 수정할 수 있습니다.");
        }

        Instant answeredAt = answer.getAnsweredAt();
        if (answeredAt != null && Duration.between(answeredAt, Instant.now()).compareTo(EDIT_LIMIT) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "답변은 작성 후 10분 이내에만 수정할 수 있습니다."
            );
        }

        answer.setAnswerText(req.getAnswerText());
        answer.setUpdatedAt(Instant.now());

        QuestionAnswer saved = answerRepository.save(answer);
        return toResponse(saved, saved.getQuestion());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionAnswerDto.MonthlyItem> getMyAnswersForMonth(User user, YearMonth ym) {
        LocalDate monthFirst = toMonthFirstDay(ym);

        return answerRepository
                .findAllWithQuestionByUserIdAndAnswerMonthOrderByQuestionDay(user.getId(), monthFirst)
                .stream()
                .map(answer -> QuestionAnswerDto.MonthlyItem.builder()
                        .answerId(answer.getId())
                        .questionId(answer.getQuestion().getId())
                        .questionContent(answer.getQuestion().getContent())
                        .questionDay(answer.getQuestion().getId().intValue())
                        .answerText(answer.getAnswerText())
                        .answeredAt(answer.getAnsweredAt())
                        .updatedAt(answer.getUpdatedAt())
                        .date(answer.getAnsweredAt().atZone(KST).toLocalDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsForMeThisMonth(User user, Long questionId, YearMonth ym) {
        LocalDate monthFirst = toMonthFirstDay(ym);

        return answerRepository
                .findByUserIdAndQuestionIdAndAnswerMonth(user.getId(), questionId, monthFirst)
                .isPresent();
    }

    private QuestionAnswerDto.Response toResponse(QuestionAnswer entity, Question question) {
        return QuestionAnswerDto.Response.builder()
                .id(entity.getId())
                .questionId(question.getId())
                .questionContent(question.getContent())
                .questionDay(question.getId().intValue())
                .answerText(entity.getAnswerText())
                .answeredAt(entity.getAnsweredAt())
                .answerMonth(entity.getAnswerMonth())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}