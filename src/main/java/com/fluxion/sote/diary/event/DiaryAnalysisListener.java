package com.fluxion.sote.diary.event;

import com.fluxion.sote.analysis.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 일기 저장 완료 후 자동 감정분석 실행 리스너
 * 트랜잭션 커밋 이후(After Commit)에 실행됨
 */
@Component
@RequiredArgsConstructor
public class DiaryAnalysisListener {

    private final AnalysisService analysisService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDiarySaved(DiarySavedEvent event) {
        analysisService.runInNewTx(event.getDiary());
    }
}
