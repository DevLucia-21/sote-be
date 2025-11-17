package com.fluxion.sote.watch.repository;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.watch.entity.WatchNotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchNotificationTokenRepository
        extends JpaRepository<WatchNotificationToken, Long> {

    List<WatchNotificationToken> findByUser(User user);

    Optional<WatchNotificationToken> findByUserAndDeviceId(User user, String deviceId);
}
