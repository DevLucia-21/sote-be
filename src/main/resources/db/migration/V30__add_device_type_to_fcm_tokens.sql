-- V19__add_device_type_to_fcm_tokens.sql
-- Adds device_type column to fcm_tokens for distinguishing MOBILE vs WATCH

ALTER TABLE fcm_tokens
    ADD COLUMN device_type VARCHAR(16);

-- Set existing rows to MOBILE
UPDATE fcm_tokens
    SET device_type = 'MOBILE'
    WHERE device_type IS NULL;

-- Add NOT NULL constraint
ALTER TABLE fcm_tokens
    ALTER COLUMN device_type SET NOT NULL;
