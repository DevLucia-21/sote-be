package com.fluxion.sote.notification.repository;

import com.fluxion.sote.notification.entity.NotificationSetting;
import com.fluxion.sote.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    /**
     * 특정 사용자에 대한 모든 알림 설정 조회
     */
    List<NotificationSetting> findByUser(User user);

    /**
     * 특정 사용자의 모든 알림 설정 삭제
     */
    void deleteByUser(User user);
}
