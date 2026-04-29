package com.fluxion.sote.setting.repository;

import com.fluxion.sote.setting.entity.NotificationSetting;
import com.fluxion.sote.setting.entity.NotificationSetting.Pk;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.setting.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Pk> {

    List<NotificationSetting> findByUser(User user);

    @Modifying
    @Transactional
    void deleteByUser(User user);

    boolean existsByUserAndNotificationType(User user, NotificationType notificationType);
}
