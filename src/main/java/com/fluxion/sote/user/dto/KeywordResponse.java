package com.fluxion.sote.user.dto;

public class KeywordResponse {

    private Long id;
    private String content;

    public KeywordResponse() {}

    public KeywordResponse(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
