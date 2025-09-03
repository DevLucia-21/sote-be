package com.fluxion.sote.diary.dto;

import com.fluxion.sote.diary.entity.WriteType;
import com.fluxion.sote.global.enums.EmotionType;
import java.time.LocalDate;
import java.util.List;

public class DiaryDto {

    private Long id;
    private LocalDate date;
    private String content;
    private WriteType writeType;
    private EmotionType emotionType;
    private String imageUrl;
    private List<String> keywords;

    public DiaryDto(Long id, LocalDate date, String content,
                    WriteType writeType, EmotionType emotionType,
                    String imageUrl, List<String> keywords) {
        this.id = id;
        this.date = date;
        this.content = content;
        this.writeType = writeType;
        this.emotionType = emotionType;
        this.imageUrl = imageUrl;
        this.keywords = keywords;
    }

    // =============== 요청 DTO ===============
    public static class CreateRequest {
        private String content;
        private LocalDate date;
        private List<Long> keywordIds;
        private EmotionType emotionType;

        public String getContent() { return content; }
        public LocalDate getDate() { return date; }
        public List<Long> getKeywordIds() { return keywordIds; }
        public EmotionType getEmotionType() { return emotionType; }
    }

    public static class UpdateRequest {
        private String content;
        private LocalDate date;
        private List<Long> keywordIds;
        private EmotionType emotionType;

        public String getContent() { return content; }
        public LocalDate getDate() { return date; }
        public List<Long> getKeywordIds() { return keywordIds; }
        public EmotionType getEmotionType() { return emotionType; }
    }

    // getter
    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getContent() { return content; }
    public WriteType getWriteType() { return writeType; }
    public EmotionType getEmotionType() { return emotionType; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getKeywords() { return keywords; }
}
