package com.fluxion.sote.setting.repository;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.setting.entity.FcmToken;
import com.fluxion.sote.setting.enums.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByToken(String token);

    boolean existsByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM FcmToken f WHERE f.token = :token")
    void deleteByToken(@Param("token") String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM FcmToken f WHERE f.user = :user")
    void deleteAllByUser(@Param("user") User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM FcmToken f WHERE f.token = :token")
    void deleteExpiredToken(@Param("token") String token);

    // 모든 기기(MOBILE + WATCH) 조회
    List<FcmToken> findAllByUser(User user);

    // 특정 기기(MOBILE 또는 WATCH)만 조회
    List<FcmToken> findAllByUserAndDeviceType(User user, DeviceType deviceType);
}