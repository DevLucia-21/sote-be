-- 1) write_type 추가 + 기본값 채우기 + NOT NULL
ALTER TABLE diaries ADD COLUMN IF NOT EXISTS write_type VARCHAR(20);
UPDATE diaries SET write_type = 'TEXT' WHERE write_type IS NULL;
ALTER TABLE diaries ALTER COLUMN write_type SET DEFAULT 'TEXT';
ALTER TABLE diaries ALTER COLUMN write_type SET NOT NULL;

-- (선택) 값 검증: TEXT / OCR / STT
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'ck_diaries_write_type'
  ) THEN
    ALTER TABLE diaries
      ADD CONSTRAINT ck_diaries_write_type
      CHECK (write_type IN ('TEXT','OCR','STT')) NOT VALID;
    ALTER TABLE diaries VALIDATE CONSTRAINT ck_diaries_write_type;
  END IF;
END$$;

-- 2) emotion_type 추가 (NULL 허용)
ALTER TABLE diaries ADD COLUMN IF NOT EXISTS emotion_type VARCHAR(30);

-- 3) image_url 추가 (NULL 허용)
ALTER TABLE diaries ADD COLUMN IF NOT EXISTS image_url TEXT;
