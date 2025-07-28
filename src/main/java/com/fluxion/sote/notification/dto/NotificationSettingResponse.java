package com.fluxion.sote.notification.dto;

import com.fluxion.sote.notification.enums.NotificationType;
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
