CREATE TABLE stt_results (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    text TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_stt_results_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);
