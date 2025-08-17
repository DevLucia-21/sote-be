package com.fluxion.sote.answer.repository;

import com.fluxion.sote.answer.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {

    boolean existsByUserIdAndQuestionIdAndAnswerMonth(Long userId, Long questionId, LocalDate answerMonth);

    Optional<QuestionAnswer> findByUserIdAndQuestionIdAndAnswerMonth(Long userId, Long questionId, LocalDate answerMonth);

    /** 월별 내 답변 + 질문 본문을 한 번에 로딩(fetch join) */
    @Query("""
       select a from QuestionAnswer a
         join fetch a.question q
        where a.user.id = :userId
          and a.answerMonth = :answerMonth
        order by q.id asc
       """)
    List<QuestionAnswer> findAllWithQuestionByUserIdAndAnswerMonthOrderByQuestionDay(Long userId, LocalDate answerMonth);


}
