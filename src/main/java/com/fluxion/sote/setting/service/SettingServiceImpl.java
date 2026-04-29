// src/main/java/com/fluxion/sote/setting/service/SettingServiceImpl.java
package com.fluxion.sote.setting.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.user.repository.UserRepository;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.setting.dto.NotificationSettingRequest;
import com.fluxion.sote.setting.dto.NotificationSettingResponse;
import com.fluxion.sote.setting.dto.ThemeSettingResponse;
import com.fluxion.sote.setting.entity.NotificationSetting;
import com.fluxion.sote.setting.enums.NotificationType;
import com.fluxion.sote.setting.repository.NotificationSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SettingServiceImpl implements SettingService {

    private final NotificationSettingRepository settingRepository;
    private final UserRepository userRepository;

    public SettingServiceImpl(
            NotificationSettingRepository settingRepository,
            UserRepository userRepository
    ) {
        this.settingRepository = settingRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationSettingResponse getMySettings() {
        User user = SecurityUtil.getCurrentUser();
        List<NotificationSetting> settings = settingRepository.findByUser(user);
        Set<NotificationType> types = settings.stream()
                .map(NotificationSetting::getNotificationType)
                .collect(Collectors.toSet());
        return new NotificationSettingResponse(types);
    }

    @Override
    @Transactional
    public void updateMySettings(NotificationSettingRequest request) {
        User user = SecurityUtil.getCurrentUser();
        // 1) 기존 설정 전부 삭제
        settingRepository.deleteByUser(user);
        // 2) 새 요청 타입 저장
        Set<NotificationType> types = request.getEnabledNotifications();
        if (types != null && !types.isEmpty()) {
            List<NotificationSetting> newSettings = types.stream()
                    .map(type -> new NotificationSetting(user, type))
                    .toList();
            settingRepository.saveAll(newSettings);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean getCurrentThemeSetting() {
        User user = SecurityUtil.getCurrentUser();
        // User 엔티티에 isDarkMode 필드가 있다고 가정
        return user.isDarkMode();
    }

    @Override
    @Transactional
    public void updateThemeSetting(boolean isDarkMode) {
        User user = SecurityUtil.getCurrentUser();
        user.setDarkMode(isDarkMode);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public ThemeSettingResponse getUserSettings() {
        return new ThemeSettingResponse(getCurrentThemeSetting());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNotificationEnabled(User user, NotificationType type) {
        return settingRepository.existsByUserAndNotificationType(user, type);
    }
}
