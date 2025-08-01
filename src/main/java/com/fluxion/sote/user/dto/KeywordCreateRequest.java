// src/main/java/com/fluxion/sote/user/dto/KeywordCreateRequest.java
package com.fluxion.sote.user.dto;

public class KeywordCreateRequest {
    private String content;

    public KeywordCreateRequest() {}

    public KeywordCreateRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
