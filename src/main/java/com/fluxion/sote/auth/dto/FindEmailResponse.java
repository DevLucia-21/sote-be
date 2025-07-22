package com.fluxion.sote.auth.dto;

public class FindEmailResponse {

    private String email;

    public FindEmailResponse() {}

    public FindEmailResponse(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
