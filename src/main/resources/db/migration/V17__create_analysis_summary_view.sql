CREATE OR REPLACE VIEW analysis_summary_v AS
SELECT
  a.id                 AS analysis_id,
  a.user_id,
  a.diary_id,
  a.birth_year,
  a.analysis_date,
  a.created_at         AS analysis_created_at,

  ar.emotion_label,
  ar.emotion_score,
  ar.emotion_reason,
  ar.music_json,
  ar.ai_response,
  ar.created_at        AS result_created_at,

  -- 선정된 1곡 (스칼라 컬럼)
  ar.selected_track_title,
  ar.selected_track_artist,
  ar.selected_track_album,
  ar.selected_track_genre,
  ar.selected_track_index,

  -- 선정된 1곡 (JSON 편의 컬럼)
  CASE
    WHEN ar.selected_track_title IS NOT NULL OR ar.selected_track_artist IS NOT NULL THEN
      jsonb_build_object(
        'title',  ar.selected_track_title,
        'artist', ar.selected_track_artist,
        'album',  ar.selected_track_album,
        'genre',  ar.selected_track_genre,
        'index',  ar.selected_track_index
      )
    ELSE NULL
  END                AS selected_track,

  COALESCE(
    jsonb_agg(jsonb_build_object('id', g.id, 'name', g.name) ORDER BY g.name)
      FILTER (WHERE g.id IS NOT NULL),
    '[]'::jsonb
  ) AS genres
FROM analysis a
LEFT JOIN analysis_result ar ON ar.analysis_id = a.id
LEFT JOIN analysis_genres ag ON ag.analysis_id = a.id
LEFT JOIN genres g           ON g.id = ag.genre_id
GROUP BY a.id, ar.id;
