// src/main/java/com/fluxion/sote/user/dto/ChangePasswordRequest.java
package com.fluxion.sote.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 로그인한 사용자가 현재 비밀번호를 검증한 뒤
 * 새 비밀번호로 변경하기 위한 요청 DTO입니다.
 */
public class ChangePasswordRequest {

    @NotBlank(message = "현재 비밀번호를 입력하세요.")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호를 입력하세요.")
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
