-- V9__add_user_related_tables.sql

-- 1) Refresh tokens
CREATE TABLE IF NOT EXISTS public.refresh_tokens (
  id           BIGSERIAL PRIMARY KEY,
  user_id      BIGINT    NOT NULL,
  token        VARCHAR(512) NOT NULL,
  issued_at    TIMESTAMP NOT NULL DEFAULT now(),
  expires_at   TIMESTAMP NOT NULL,
  CONSTRAINT fk_refresh_user
    FOREIGN KEY(user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- 2) User profiles
CREATE TABLE IF NOT EXISTS public.user_profiles (
  user_id      BIGINT    PRIMARY KEY,
  image_data   BYTEA,
  image_url    VARCHAR(255),
  bio          TEXT,
  CONSTRAINT fk_profile_user
    FOREIGN KEY(user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- 3) User settings
CREATE TABLE IF NOT EXISTS public.user_settings (
  user_id             BIGINT     PRIMARY KEY,
  notify_diary        BOOLEAN    NOT NULL DEFAULT TRUE,
  notify_challenge    BOOLEAN    NOT NULL DEFAULT TRUE,
  notify_emotion_done BOOLEAN    NOT NULL DEFAULT TRUE,
  notify_music_recommend BOOLEAN  NOT NULL DEFAULT TRUE,
  notify_weekly_stats BOOLEAN    NOT NULL DEFAULT TRUE,
  theme               VARCHAR(50) NOT NULL DEFAULT 'light',
  CONSTRAINT fk_settings_user
    FOREIGN KEY(user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- 4) User keywords
CREATE TABLE IF NOT EXISTS public.user_keywords (
  id         BIGSERIAL PRIMARY KEY,
  user_id    BIGINT    NOT NULL,
  keyword    VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  CONSTRAINT fk_keywords_user
    FOREIGN KEY(user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- 5) FCM tokens
CREATE TABLE IF NOT EXISTS public.fcm_tokens (
  id             BIGSERIAL PRIMARY KEY,
  user_id        BIGINT    NOT NULL,
  token          VARCHAR(255) NOT NULL,
  registered_at  TIMESTAMP NOT NULL DEFAULT now(),
  CONSTRAINT fk_fcm_user
    FOREIGN KEY(user_id) REFERENCES public.users(id) ON DELETE CASCADE
);
