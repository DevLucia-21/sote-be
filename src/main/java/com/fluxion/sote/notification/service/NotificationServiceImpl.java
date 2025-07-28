package com.fluxion.sote.notification.service;

import com.fluxion.sote.notification.entity.NotificationSetting;
import com.fluxion.sote.notification.dto.NotificationSettingRequest;
import com.fluxion.sote.notification.dto.NotificationSettingResponse;
import com.fluxion.sote.notification.enums.NotificationType;
import com.fluxion.sote.notification.repository.NotificationSettingRepository;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationSettingRepository settingRepository;

    public NotificationServiceImpl(NotificationSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    /**
     * 로그인한 사용자의 알림 설정 목록 조회
     */
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

    /**
     * 로그인한 사용자의 알림 설정 목록 저장 (전체 초기화 후 재등록)
     */
    @Override
    @Transactional
    public void updateMySettings(NotificationSettingRequest request) {
        User user = SecurityUtil.getCurrentUser();

        // 기존 설정 제거
        settingRepository.deleteByUser(user);

        // 새로운 설정 저장
        Set<NotificationType> types = request.getEnabledNotifications();
        if (types != null && !types.isEmpty()) {
            List<NotificationSetting> newSettings = types.stream()
                    .map(type -> new NotificationSetting(user, type))
                    .toList();
            settingRepository.saveAll(newSettings);
        }
    }
}
