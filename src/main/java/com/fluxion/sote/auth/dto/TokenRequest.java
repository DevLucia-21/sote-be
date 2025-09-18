// src/main/java/com/fluxion/sote/auth/dto/TokenRequest.java
package com.fluxion.sote.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {
    private String refreshToken;
}
