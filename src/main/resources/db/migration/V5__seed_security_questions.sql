-- V5__seed_security_questions.sql
INSERT INTO security_questions (id, question_text) VALUES
  (1, '처음 키운 반려동물 이름은?'),
  (2, '졸업한 초등학교 이름은?'),
  (3, '출생 도시 이름은?'),
  (4, '어렸을 적 별명은?'),
  (5, '첫 번째 휴대전화 기종은?'),
  (6, '당신이 가장 좋아하는 달은?'),
  (7, '당신의 좌우명은?'),
  (8, '가장 의지했던 사람의 이름은?'),
  (9, '어린 시절 꿈은?'),
  (10, '처음으로 가본 여행지는?')
ON CONFLICT (id) DO NOTHING;
