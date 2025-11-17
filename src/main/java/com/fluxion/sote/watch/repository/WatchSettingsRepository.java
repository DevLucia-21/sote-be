package com.fluxion.sote.watch.repository;
// src/main/java/com/fluxion/sote/watch/repository/WatchSettingsRepository.java

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.watch.entity.WatchSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchSettingsRepository extends JpaRepository<WatchSettings, Long> {

    Optional<WatchSettings> findByUser(User user);
}
