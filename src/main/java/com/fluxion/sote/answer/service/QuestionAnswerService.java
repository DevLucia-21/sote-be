package com.fluxion.sote.answer.service;

import com.fluxion.sote.answer.dto.QuestionAnswerDto;
import com.fluxion.sote.auth.entity.User;

import java.time.YearMonth;
import java.util.List;

public interface QuestionAnswerService {

    /** 작성만 가능(수정 불가). 같은 달 같은 질문에 이미 있으면 409 */
    QuestionAnswerDto.Response create(User user, Long questionId, QuestionAnswerDto.CreateRequest req);

    /** 답변 수정 (10분 이내만 가능) */
    QuestionAnswerDto.Response update(User user, Long answerId, QuestionAnswerDto.UpdateRequest req);

    /** 월별 내 답변 리스트 (fetch join) */
    List<QuestionAnswerDto.MonthlyItem> getMyAnswersForMonth(User user, YearMonth ym);

    /** 특정 질문에 대해 해당 월에 내가 작성했는지 여부 */
    boolean existsForMeThisMonth(User user, Long questionId, YearMonth ym);
}
