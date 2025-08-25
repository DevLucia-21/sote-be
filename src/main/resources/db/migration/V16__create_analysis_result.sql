CREATE TABLE IF NOT EXISTS analysis_result (
    id                     BIGSERIAL PRIMARY KEY,
    analysis_id            BIGINT NOT NULL REFERENCES analysis(id) ON DELETE CASCADE,

    -- 감정 요약
    emotion_label          VARCHAR(50),
    emotion_score          NUMERIC(5,4),
    emotion_reason         TEXT,

    -- 선정된 1곡(중복 회피 후 최종 선택)
    selected_track_title   VARCHAR(255),
    selected_track_artist  VARCHAR(255),
    selected_track_album   VARCHAR(255),
    selected_track_genre   VARCHAR(100),
    selected_track_index   INT,                -- 원본 music 배열의 인덱스(0-based)

    -- 원본 데이터
    music_json             JSONB,              -- 추천 음악 배열 전체(3곡)
    ai_response            JSONB,              -- AI 응답 RAW

    -- 생성 시각(정렬/감사용)
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 한 분석당 결과 1건 보장
CREATE UNIQUE INDEX IF NOT EXISTS ux_analysis_result_analysis_id
    ON analysis_result (analysis_id);

-- 최근 결과 정렬/조회
CREATE INDEX IF NOT EXISTS idx_analysis_result_created
    ON analysis_result (created_at);

-- 최근 3일 중복 회피용(제목+아티스트 대소문자 무시)
CREATE INDEX IF NOT EXISTS idx_analysis_result_selected_ci
    ON analysis_result (lower(selected_track_title), lower(selected_track_artist));
