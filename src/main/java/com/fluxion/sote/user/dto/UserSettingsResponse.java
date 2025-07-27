package com.fluxion.sote.user.dto;

import com.fluxion.sote.user.enums.NotificationType;
import java.util.Set;

public class UserSettingsResponse {

    private Set<NotificationType> enabledNotifications;

    public UserSettingsResponse(Set<NotificationType> enabledNotifications) {
        this.enabledNotifications = enabledNotifications;
    }

    public Set<NotificationType> getEnabledNotifications() {
        return enabledNotifications;
    }

    public void setEnabledNotifications(Set<NotificationType> enabledNotifications) {
        this.enabledNotifications = enabledNotifications;
    }
}
