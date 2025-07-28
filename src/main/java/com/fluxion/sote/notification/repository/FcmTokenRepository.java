package com.fluxion.sote.notification.repository;

import com.fluxion.sote.notification.entity.FcmToken;
import com.fluxion.sote.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByToken(String token);
    boolean existsByToken(String token);
    void deleteByToken(String token);
    void deleteAllByUser(User user);
}
