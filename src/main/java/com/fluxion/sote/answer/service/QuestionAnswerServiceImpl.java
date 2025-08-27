package com.fluxion.sote.answer.service;

import com.fluxion.sote.answer.dto.QuestionAnswerDto;
import com.fluxion.sote.answer.entity.QuestionAnswer;
import com.fluxion.sote.answer.repository.QuestionAnswerRepository;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.question.entity.Question;
import com.fluxion.sote.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.stream.Collectors;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionAnswerServiceImpl implements QuestionAnswerService {

    private final QuestionRepository questionRepository;
    private final QuestionAnswerRepository answerRepository;

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final Duration RETRACT_WINDOW = Duration.ofMinutes(10);
    private static final DateTimeFormatter YM_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private LocalDate toMonthFirstDay(YearMonth ym) {
        return ym.atDay(1);
    }

    private YearMonth parseOrNow(String yyyyMM) {
        return (yyyyMM == null || yyyyMM.isBlank())
                ? YearMonth.now(ZONE)
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
                        .answeredAt(LocalDateTime.now(ZONE))   // 타임존 고정
                        .answerMonth(monthFirst)
                        .build()
        );

        return QuestionAnswerDto.Response.builder()
                .id(saved.getId())
                .questionId(q.getId())
                .questionContent(q.getContent())
                .questionDay(q.getId().intValue())
                .answerText(saved.getAnswerText())
                .answeredAt(saved.getAnsweredAt())
                .answerMonth(saved.getAnswerMonth())
                .build();
    }

    @Override
    public void delete(User user, Long answerId) {
        QuestionAnswer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "답변을 찾을 수 없습니다."));

        // 본인만 삭제 가능
        if (!a.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 답변만 철회할 수 있습니다.");
        }

        // 10분 이내 철회만 허용
        LocalDateTime now = LocalDateTime.now(ZONE);
        Duration elapsed = Duration.between(a.getAnsweredAt(), now);
        if (elapsed.compareTo(RETRACT_WINDOW) > 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성 후 10분이 지나 철회할 수 없습니다.");
        }

        answerRepository.delete(a);
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
                        .questionDay(a.getQuestion().getId().intValue()) // id를 날짜처럼 사용
                        .answerText(a.getAnswerText())
                        .answeredAt(a.getAnsweredAt())
                        .date(a.getAnsweredAt().toLocalDate())
                        .build())
                .collect(Collectors.toList());                     // ✅ 제네릭 명확화
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existsForMeThisMonth(User user, Long questionId, YearMonth ym) {
        LocalDate monthFirst = toMonthFirstDay(ym);
        return answerRepository.existsByUserIdAndQuestionIdAndAnswerMonth(user.getId(), questionId, monthFirst);
    }
}
