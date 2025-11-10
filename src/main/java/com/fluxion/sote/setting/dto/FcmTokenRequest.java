// src/main/java/com/fluxion/sote/setting/dto/FcmTokenRequest.java
package com.fluxion.sote.setting.dto;

import com.fluxion.sote.setting.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FcmTokenRequest {

    @NotBlank
    private String token;

    @NotNull
    private DeviceType deviceType = DeviceType.MOBILE; // 기본값은 MOBILE

    public FcmTokenRequest() {}

    public FcmTokenRequest(String token, DeviceType deviceType) {
        this.token = token;
        this.deviceType = deviceType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}
