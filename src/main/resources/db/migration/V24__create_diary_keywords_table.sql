-- 다이어리-키워드 조인 테이블 생성
CREATE TABLE diary_keywords (
    diary_id BIGINT NOT NULL,
    keyword_id BIGINT NOT NULL,
    PRIMARY KEY (diary_id, keyword_id),

    CONSTRAINT fk_diary_keywords_diary
        FOREIGN KEY (diary_id) REFERENCES diaries (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_diary_keywords_keyword
        FOREIGN KEY (keyword_id) REFERENCES keywords (id)
        ON DELETE CASCADE
);

-- 성능 최적화를 위한 인덱스 추가
CREATE INDEX idx_diary_keywords_diary ON diary_keywords(diary_id);
CREATE INDEX idx_diary_keywords_keyword ON diary_keywords(keyword_id);
