-- V10__add_keywords_table.sql

CREATE TABLE IF NOT EXISTS public.keywords (
  id         BIGSERIAL PRIMARY KEY,
  content    VARCHAR(50) NOT NULL,
  user_id    BIGINT    NOT NULL,
  CONSTRAINT fk_keywords_user
    FOREIGN KEY(user_id) REFERENCES public.users(id) ON DELETE CASCADE
);
