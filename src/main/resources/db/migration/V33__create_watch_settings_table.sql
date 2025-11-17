CREATE TABLE IF NOT EXISTS watch_settings (
    watch_settings_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,

    -- 알림 설정
    notify_hrv BOOLEAN NOT NULL DEFAULT TRUE,
    notify_health_sync BOOLEAN NOT NULL DEFAULT TRUE,
    notify_diary BOOLEAN NOT NULL DEFAULT TRUE,
    notify_challenge BOOLEAN NOT NULL DEFAULT TRUE,

    -- 데이터 전송 옵션
    wifi_only BOOLEAN NOT NULL DEFAULT FALSE,
    auto_sync BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_watch_settings_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

-- 한 유저당 1개의 워치 설정만 허용 (중복 방지)
CREATE UNIQUE INDEX IF NOT EXISTS ux_watch_settings_user
    ON watch_settings (user_id);
