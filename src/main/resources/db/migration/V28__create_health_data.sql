CREATE TABLE IF NOT EXISTS health_data (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    heart_rate DOUBLE PRECISION,
    hrv DOUBLE PRECISION,
    steps INTEGER,
    measured_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT idx_user_measured_at UNIQUE (user_id, measured_at)
);
