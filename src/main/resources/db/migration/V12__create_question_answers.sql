CREATE TABLE question_answers (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT      NOT NULL,
    question_id   BIGINT      NOT NULL,
    answer_text   TEXT        NOT NULL,
    answered_at   TIMESTAMP   NOT NULL DEFAULT NOW(),  -- 작성 시각 (Asia/Seoul 기준으로 서비스단에서 세팅)
    answer_month  DATE        NOT NULL,                -- 해당 월의 1일 (예: 2025-08-01)

    CONSTRAINT fk_answer_user     FOREIGN KEY (user_id)    REFERENCES users(id),
    CONSTRAINT fk_answer_question FOREIGN KEY (question_id) REFERENCES questions(id),
    CONSTRAINT uq_user_q_month UNIQUE (user_id, question_id, answer_month)
);

-- 월별/유저별 조회와 조인 최적화
CREATE INDEX idx_answers_user_month ON question_answers(user_id, answer_month);
CREATE INDEX idx_answers_question_month ON question_answers(question_id, answer_month);
