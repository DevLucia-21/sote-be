package com.fluxion.sote.setting.service;

import com.fluxion.sote.setting.dto.NotificationSettingRequest;
import com.fluxion.sote.setting.dto.NotificationSettingResponse;
import com.fluxion.sote.setting.dto.ThemeSettingResponse;

public interface SettingService {

    NotificationSettingResponse getMySettings();
    ThemeSettingResponse getUserSettings();

    void updateMySettings(NotificationSettingRequest request);
    boolean getCurrentThemeSetting();
    void updateThemeSetting(boolean isDarkMode);
}
