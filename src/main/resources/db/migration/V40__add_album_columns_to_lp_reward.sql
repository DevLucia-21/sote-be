-- LP 보상 테이블에 앨범 관련 컬럼 추가
ALTER TABLE lp_reward
    ADD COLUMN IF NOT EXISTS album VARCHAR(200);

ALTER TABLE lp_reward
    ADD COLUMN IF NOT EXISTS album_image_url VARCHAR(500);

ALTER TABLE lp_reward
    ADD COLUMN IF NOT EXISTS play_url VARCHAR(500);
