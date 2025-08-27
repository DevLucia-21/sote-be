-- V6__seed_user_security_answers.sql
INSERT INTO user_security_answers (user_id, question_id, answer_encrypted) VALUES
  (1, 1, '$2a$10$GxqmQz49AZiSt52hXXkrGuVAYP6NgEyS/.DCFwAMJ6/YhpKMf5AH2')
ON CONFLICT (user_id, question_id) DO NOTHING;
