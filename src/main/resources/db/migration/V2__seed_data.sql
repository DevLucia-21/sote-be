-- 1) 기본 장르 데이터 삽입
INSERT INTO public.genres (name) VALUES
  ('rock'),
  ('pop'),
  ('jazz'),
  ('classical'),
  ('hiphop'),
  ('electronic')
ON CONFLICT (name) DO NOTHING;

-- 2) (선택) 개발 편의용 더미 계정 삽입
INSERT INTO public.users (
  email,
  password,
  nickname,
  role,
  birth_date,
  security_answer,      -- 추가
  music_preferences,
  character
)
VALUES (
  'rlxkemd21@gmail.com',
  -- BCryptPasswordEncoder.encode("pass1234")로 생성한 암호 해시 예시
  '$2a$10$RPUXf.IMjszb5Q6t9EzyZ.Z2Nq.46zBoOJVdCzzLlzlLhS3sxjLjK',
  'tester',
  'ROLE_USER',
  '1990-01-01',
  'defaultAnswer',      -- 보안 질문 기본 답변
  ARRAY['rock','pop','jazz'],
  'piano'
)
ON CONFLICT (email) DO NOTHING;
