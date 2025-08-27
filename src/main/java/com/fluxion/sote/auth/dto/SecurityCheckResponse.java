package com.fluxion.sote.auth.dto;

public class SecurityCheckResponse {
    private boolean matched;

    public SecurityCheckResponse(boolean matched) {
        this.matched = matched;
    }

    // getter
    public boolean isMatched() { return matched; }
}
