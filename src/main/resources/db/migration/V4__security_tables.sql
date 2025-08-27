-- V4__security_tables.sql
CREATE TABLE security_questions (
  id            SERIAL PRIMARY KEY,
  question_text VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE user_security_answers (
  id               SERIAL PRIMARY KEY,
  user_id          BIGINT NOT NULL,
  question_id      INT    NOT NULL,
  answer_encrypted VARCHAR(255) NOT NULL,
  CONSTRAINT fk_user
    FOREIGN KEY(user_id) REFERENCES users(id),
  CONSTRAINT fk_question
    FOREIGN KEY(question_id) REFERENCES security_questions(id),
  CONSTRAINT uq_user_question UNIQUE(user_id, question_id)
);
