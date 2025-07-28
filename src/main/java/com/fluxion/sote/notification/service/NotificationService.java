package com.fluxion.sote.notification.service;

import com.fluxion.sote.notification.dto.NotificationSettingRequest;
import com.fluxion.sote.notification.dto.NotificationSettingResponse;

public interface NotificationService {

    NotificationSettingResponse getMySettings();

    void updateMySettings(NotificationSettingRequest request);
}
