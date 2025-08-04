package com.fluxion.sote.diary.dto;

import java.time.LocalDate;

public record DiaryDto(
        Long id,
        LocalDate date,
        String content


) {
    public static class CreateRequest {
        private String content;
        private LocalDate date;
        // ⚠️ 꼭 있어야 함
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
    }

    public static class UpdateRequest {
        private String content;
        private LocalDate date;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
    }
}