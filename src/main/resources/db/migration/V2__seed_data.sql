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
  character
)
VALUES (
  'test@example.com',
  '$2a$10$RPUXf.IMjszb5Q6t9EzyZ.Z2Nq.46zBoOJVdCzzLlzlLhS3sxjLjK',
  'tester',
  'ROLE_USER',
  '2000-07-01',
  'PIANO'
)
ON CONFLICT (email) DO NOTHING;

-- 3) 장르 연결 (예: pop(id=2), jazz(id=3))
-- 유저 id 가져오기
-- 예: user_id = 1이라고 가정
INSERT INTO public.user_genres (user_id, genre_id)
VALUES (1, 2), (1, 3)
ON CONFLICT DO NOTHING;