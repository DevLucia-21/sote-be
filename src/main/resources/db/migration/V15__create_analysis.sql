CREATE TABLE IF NOT EXISTS analysis (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT       NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    diary_id       BIGINT           NULL REFERENCES diaries(id) ON DELETE SET NULL,
    birth_year     INT          NOT NULL,

    -- 하루 1회 제한용 일자 (기본 KST '오늘')
    analysis_date  DATE         NOT NULL DEFAULT (now() AT TIME ZONE 'Asia/Seoul')::date,

    -- 정렬/리포팅용 (업데이트 칼럼은 생략)
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- 같은 날 같은 유저는 1건만
CREATE UNIQUE INDEX IF NOT EXISTS ux_analysis_user_day ON analysis (user_id, analysis_date);

-- 사용자별 최신 조회 최적화
CREATE INDEX IF NOT EXISTS idx_analysis_user_created ON analysis (user_id, created_at DESC);

-- 분석 시 선택된 장르 스냅샷(다대다)
-- Genre.id = INTEGER 이므로 FK도 INTEGER로 맞춘다.
CREATE TABLE IF NOT EXISTS analysis_genres (
    analysis_id BIGINT   NOT NULL REFERENCES analysis(id) ON DELETE CASCADE,
    genre_id    INTEGER  NOT NULL REFERENCES genres(id)   ON DELETE CASCADE,
    PRIMARY KEY (analysis_id, genre_id)
);

-- (옵션) 장르 역방향 조회 최적화
CREATE INDEX IF NOT EXISTS idx_analysis_genres_genre ON analysis_genres (genre_id);
