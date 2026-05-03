package com.fluxion.sote.setting.repository;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.setting.entity.NotificationSetting;
import com.fluxion.sote.setting.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, NotificationSetting.Pk> {

    List<NotificationSetting> findByUser(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM NotificationSetting ns WHERE ns.user = :user")
    void deleteByUser(@Param("user") User user);

    boolean existsByUserAndNotificationType(User user, NotificationType notificationType);

    @Query("SELECT ns.user FROM NotificationSetting ns WHERE ns.notificationType = :notificationType")
    List<User> findUsersByNotificationType(@Param("notificationType") NotificationType notificationType);
}