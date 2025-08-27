-- V21__create_user_challenge.sql

-- 유저 챌린지 테이블 생성
CREATE TABLE user_challenge (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    challenge_id BIGINT NOT NULL,
    date DATE NOT NULL, -- 추천 날짜
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP,

    CONSTRAINT fk_user_challenge_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_challenge_challenge FOREIGN KEY (challenge_id) REFERENCES challenge_definition(id) ON DELETE CASCADE,
    CONSTRAINT ux_user_challenge UNIQUE (user_id, date) -- 하루에 하나만 추천
);

-- 인덱스 추가
CREATE INDEX idx_user_challenge_user_id ON user_challenge(user_id);
CREATE INDEX idx_user_challenge_challenge_id ON user_challenge(challenge_id);
CREATE INDEX idx_user_challenge_date ON user_challenge(date);
