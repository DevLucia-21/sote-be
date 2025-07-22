package com.fluxion.sote.auth.dto;

public class FindPwdResponse {

    private String password;

    public FindPwdResponse() {}

    public FindPwdResponse(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
