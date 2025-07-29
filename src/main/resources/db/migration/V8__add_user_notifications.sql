CREATE TABLE IF NOT EXISTS public.user_notifications (
  user_id            BIGINT    NOT NULL,
  notification_type  VARCHAR(50) NOT NULL,
  CONSTRAINT fk_user_notifications_user
    FOREIGN KEY(user_id) REFERENCES public.users(id)
);
