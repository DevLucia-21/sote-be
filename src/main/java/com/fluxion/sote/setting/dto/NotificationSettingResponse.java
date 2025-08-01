package com.fluxion.sote.setting.dto;

import com.fluxion.sote.setting.enums.NotificationType;
import java.util.Set;

public class NotificationSettingResponse {

    private Set<NotificationType> enabledNotifications;

    public NotificationSettingResponse(Set<NotificationType> enabledNotifications) {
        this.enabledNotifications = enabledNotifications;
    }

    public Set<NotificationType> getEnabledNotifications() {
        return enabledNotifications;
    }

    public void setEnabledNotifications(Set<NotificationType> enabledNotifications) {
        this.enabledNotifications = enabledNotifications;
    }
}
