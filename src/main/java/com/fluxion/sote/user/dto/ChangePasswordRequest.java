// src/main/java/com/fluxion/sote/user/dto/ChangePasswordRequest.java
package com.fluxion.sote.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 로그인한 사용자가 현재 비밀번호를 검증한 뒤
 * 새 비밀번호로 변경하기 위한 요청 DTO입니다.
 *
 * Flow:
 * 1. 사용자 입력: oldPassword, newPassword
 * 2. 서비스: oldPassword 검증 → newPassword로 업데이트
 * 3. Controller: 성공 시 204 No Content 반환
 */
public class ChangePasswordRequest {

    @NotBlank(message = "현재 비밀번호를 입력하세요.")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호를 입력하세요.")
    @Size(min = 8, message = "새 비밀번호는 최소 8자 이상이어야 합니다.")
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
