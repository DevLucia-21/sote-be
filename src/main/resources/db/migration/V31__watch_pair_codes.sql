CREATE TABLE watch_pair_codes (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(16) NOT NULL UNIQUE,
    user_id     BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at  TIMESTAMP   NOT NULL,
    used        BOOLEAN     NOT NULL,
    used_at     TIMESTAMP,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);
