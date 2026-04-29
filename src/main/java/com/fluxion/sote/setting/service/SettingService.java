package com.fluxion.sote.setting.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.setting.dto.NotificationSettingRequest;
import com.fluxion.sote.setting.dto.NotificationSettingResponse;
import com.fluxion.sote.setting.dto.ThemeSettingResponse;
import com.fluxion.sote.setting.enums.NotificationType;

public interface SettingService {

    NotificationSettingResponse getMySettings();
    ThemeSettingResponse getUserSettings();

    void updateMySettings(NotificationSettingRequest request);
    boolean getCurrentThemeSetting();
    void updateThemeSetting(boolean isDarkMode);
    boolean isNotificationEnabled(User user, NotificationType type);
}
