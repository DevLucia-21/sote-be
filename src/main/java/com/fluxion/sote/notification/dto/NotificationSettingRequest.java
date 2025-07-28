package com.fluxion.sote.notification.dto;

import com.fluxion.sote.notification.enums.NotificationType;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class NotificationSettingRequest {

    @NotNull
    private Set<NotificationType> enabledNotifications;

    public Set<NotificationType> getEnabledNotifications() {
        return enabledNotifications;
    }

    public void setEnabledNotifications(Set<NotificationType> enabledNotifications) {
        this.enabledNotifications = enabledNotifications;
    }
}
