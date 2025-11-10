ALTER TABLE health_data
  ADD CONSTRAINT uk_user_measured_at UNIQUE (user_id, measured_at);
