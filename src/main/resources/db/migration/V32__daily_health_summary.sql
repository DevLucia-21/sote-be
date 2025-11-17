CREATE TABLE daily_health_summary (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    date DATE NOT NULL,
    steps BIGINT,
    avg_heart_rate DOUBLE PRECISION,
    avg_hrv_rmssd DOUBLE PRECISION,
    sleep_minutes BIGINT,
    water_ml DOUBLE PRECISION,
    caffeine_mg DOUBLE PRECISION,
    CONSTRAINT uq_user_date UNIQUE (user_id, date),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);
