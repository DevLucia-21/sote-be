-- questions 테이블에 question_day 컬럼 추가
ALTER TABLE questions
ADD COLUMN question_day INT NOT NULL DEFAULT 1;

-- 기존 데이터 업데이트 (id 값을 question_day로 매핑)
UPDATE questions
SET question_day = id;