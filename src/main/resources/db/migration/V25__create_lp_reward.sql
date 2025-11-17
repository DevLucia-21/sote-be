-- V25__create_lp_reward.sql

-- LP 보상 테이블 생성
CREATE TABLE lp_reward (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    diary_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    artist VARCHAR(150) NOT NULL,
    album_image_url VARCHAR(500),
    play_url VARCHAR(500),
    recommended_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    reward_date DATE NOT NULL DEFAULT (now() AT TIME ZONE 'Asia/Seoul')::date,

    CONSTRAINT fk_lp_reward_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_lp_reward_diary FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE,

    -- 하루 1곡 제한: 같은 유저는 같은 날짜에 하나만
    CONSTRAINT ux_lp_reward_user_date UNIQUE (user_id, reward_date)
);

-- 사용자별 최신 보상 빠르게 조회 (오늘/최근 LP 확인용)
CREATE INDEX idx_lp_reward_user_date
    ON lp_reward (user_id, recommended_at DESC);

-- 기간 조회 최적화 (주간/월간 통계용)
CREATE INDEX idx_lp_reward_reward_date
    ON lp_reward (reward_date);
