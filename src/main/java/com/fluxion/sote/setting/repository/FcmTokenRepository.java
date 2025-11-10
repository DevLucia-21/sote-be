// src/main/java/com/fluxion/sote/setting/repository/FcmTokenRepository.java
package com.fluxion.sote.setting.repository;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.setting.entity.FcmToken;
import com.fluxion.sote.setting.enums.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByToken(String token);

    boolean existsByToken(String token);

    void deleteByToken(String token);

    void deleteAllByUser(User user);

    //모든 기기(MOBILE+WATCH) 조회
    List<FcmToken> findAllByUser(User user);

    //특정 기기(MOBILE 또는 WATCH)만 조회
    List<FcmToken> findAllByUserAndDeviceType(User user, DeviceType deviceType);
}
