ALTER TABLE diaries DROP CONSTRAINT IF EXISTS uk_user_date;
ALTER TABLE analysis DROP CONSTRAINT IF EXISTS ux_analysis_user_day;
ALTER TABLE user_challenge DROP CONSTRAINT IF EXISTS uq_user_challenge;
ALTER TABLE lp_reward DROP CONSTRAINT IF EXISTS ux_lp_reward_user_date;