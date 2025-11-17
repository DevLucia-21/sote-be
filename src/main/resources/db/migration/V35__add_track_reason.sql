ALTER TABLE analysis_result
    ADD COLUMN IF NOT EXISTS selected_track_reason           text,
    ADD COLUMN IF NOT EXISTS selected_track_cover_image_url  varchar(512);
