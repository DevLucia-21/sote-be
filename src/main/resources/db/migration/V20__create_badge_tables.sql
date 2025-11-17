-- 뱃지 정의 테이블
CREATE TABLE badge_definition (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,              -- 뱃지 이름
    description VARCHAR(255),                -- 뱃지 설명
    emotion_type VARCHAR(20),                -- 특정 감정 타입 (JOY, SADNESS, ANGER, APATHY, SENSITIVE, NULL = 전체 공용/카테고리)
    category VARCHAR(50),                    -- 카테고리 기반 뱃지 (NULL이면 감정 기반)
    condition_count INT NOT NULL,            -- 몇 회 완료해야 뱃지 지급되는지
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 유저 뱃지 획득 테이블
CREATE TABLE user_badge (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    badge_definition_id BIGINT NOT NULL,     -- 이름 수정 (badge_id → badge_definition_id)
    awarded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_badge_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_badge_badge FOREIGN KEY (badge_definition_id) REFERENCES badge_definition(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_badge UNIQUE (user_id, badge_definition_id) -- 동일 뱃지 중복 방지
);

-- 인덱스
CREATE INDEX idx_badge_definition_emotion_type ON badge_definition(emotion_type);
CREATE INDEX idx_badge_definition_category ON badge_definition(category);
CREATE INDEX idx_user_badge_user_id ON user_badge(user_id);
CREATE INDEX idx_user_badge_badge_id ON user_badge(badge_definition_id);

-- 공용 뱃지
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('챌린지 입문자', '어떤 챌린지든 1회 완료', NULL, NULL, 1);

-- JOY (기쁨)
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('기쁨의 달인 I', '기쁨 챌린지를 10회 완료', 'JOY', NULL, 10),
('기쁨의 달인 II', '기쁨 챌린지를 20회 완료', 'JOY', NULL, 20),
('기쁨의 달인 III', '기쁨 챌린지를 30회 완료', 'JOY', NULL, 30),
('기쁨의 마스터', '기쁨 챌린지를 50회 이상 완료', 'JOY', NULL, 50);

-- SADNESS (슬픔)
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('슬픔 정복자 I', '슬픔 챌린지를 10회 완료', 'SADNESS', NULL, 10),
('슬픔 정복자 II', '슬픔 챌린지를 20회 완료', 'SADNESS', NULL, 20),
('슬픔 정복자 III', '슬픔 챌린지를 30회 완료', 'SADNESS', NULL, 30),
('슬픔 정복 마스터', '슬픔 챌린지를 50회 이상 완료', 'SADNESS', NULL, 50);

-- ANGER (화남)
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('분노 조절의 달인 I', '화남 챌린지를 10회 완료', 'ANGER', NULL, 10),
('분노 조절의 달인 II', '화남 챌린지를 20회 완료', 'ANGER', NULL, 20),
('분노 조절의 달인 III', '화남 챌린지를 30회 완료', 'ANGER', NULL, 30),
('분노 조절의 마스터', '화남 챌린지를 50회 이상 완료', 'ANGER', NULL, 50);

-- APATHY (무기력)
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('무기력 극복자 I', '무기력 챌린지를 10회 완료', 'APATHY', NULL, 10),
('무기력 극복자 II', '무기력 챌린지를 20회 완료', 'APATHY', NULL, 20),
('무기력 극복자 III', '무기력 챌린지를 30회 완료', 'APATHY', NULL, 30),
('무기력 극복 마스터', '무기력 챌린지를 50회 이상 완료', 'APATHY', NULL, 50);

-- SENSITIVE (불안)
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('불안 극복자 I', '불안 챌린지를 10회 완료', 'SENSITIVE', NULL, 10),
('불안 극복자 II', '불안 챌린지를 20회 완료', 'SENSITIVE', NULL, 20),
('불안 극복자 III', '불안 챌린지를 30회 완료', 'SENSITIVE', NULL, 30),
('불안 극복 마스터', '불안 챌린지를 50회 이상 완료', 'SENSITIVE', NULL, 50);

-- 운동 업적 (카테고리 기반)
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('운동의 달인 I', '운동 챌린지를 10회 완료', NULL, '운동', 10),
('운동의 달인 II', '운동 챌린지를 20회 완료', NULL, '운동', 20),
('운동의 달인 III', '운동 챌린지를 30회 완료', NULL, '운동', 30),
('운동 마스터', '운동 챌린지를 50회 이상 완료', NULL, '운동', 50);

-- 루틴 업적
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('루틴의 달인 I', '루틴 카테고리 챌린지를 10회 완료', NULL, '루틴', 10),
('루틴의 달인 II', '루틴 카테고리 챌린지를 20회 완료', NULL, '루틴', 20),
('루틴의 달인 III', '루틴 카테고리 챌린지를 30회 완료', NULL, '루틴', 30),
('루틴 마스터', '루틴 카테고리 챌린지를 50회 이상 완료', NULL, '루틴', 50);

-- 도전 업적
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('도전 정신 I', '도전 카테고리 챌린지를 10회 완료', NULL, '도전', 10),
('도전 정신 II', '도전 카테고리 챌린지를 20회 완료', NULL, '도전', 20),
('도전 정신 III', '도전 카테고리 챌린지를 30회 완료', NULL, '도전', 30),
('도전 마스터', '도전 카테고리 챌린지를 50회 이상 완료', NULL, '도전', 50);

-- 음악 업적
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('음악 애호가 I', '음악 카테고리 챌린지를 10회 완료', NULL, '음악', 10),
('음악 애호가 II', '음악 카테고리 챌린지를 20회 완료', NULL, '음악', 20),
('음악 애호가 III', '음악 카테고리 챌린지를 30회 완료', NULL, '음악', 30),
('음악 마스터', '음악 카테고리 챌린지를 50회 이상 완료', NULL, '음악', 50);

-- 휴식 업적
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('휴식의 달인 I', '휴식 카테고리 챌린지를 10회 완료', NULL, '휴식', 10),
('휴식의 달인 II', '휴식 카테고리 챌린지를 20회 완료', NULL, '휴식', 20),
('휴식의 달인 III', '휴식 카테고리 챌린지를 30회 완료', NULL, '휴식', 30),
('휴식 마스터', '휴식 카테고리 챌린지를 50회 이상 완료', NULL, '휴식', 50);

-- 창작 업적
INSERT INTO badge_definition (name, description, emotion_type, category, condition_count) VALUES
('창작의 달인 I', '창작 카테고리 챌린지를 10회 완료', NULL, '창작', 10),
('창작의 달인 II', '창작 카테고리 챌린지를 20회 완료', NULL, '창작', 20),
('창작의 달인 III', '창작 카테고리 챌린지를 30회 완료', NULL, '창작', 30),
('창작 마스터', '창작 카테고리 챌린지를 50회 이상 완료', NULL, '창작', 50);
