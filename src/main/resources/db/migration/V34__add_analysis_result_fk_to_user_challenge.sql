-- user_challenge 테이블에 analysis_result_id 컬럼 추가
ALTER TABLE user_challenge
    ADD COLUMN analysis_result_id BIGINT;

-- FK 설정 (analysis_result 테이블 이름은 실제 DDL에 맞게 확인)
ALTER TABLE user_challenge
    ADD CONSTRAINT fk_user_challenge_analysis_result
        FOREIGN KEY (analysis_result_id) REFERENCES analysis_result (id);
