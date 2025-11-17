// src/main/java/com/fluxion/sote/watch/service/impl/WatchLogoutServiceImpl.java
package com.fluxion.sote.watch.service.impl;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.user.repository.UserRepository;
import com.fluxion.sote.watch.dto.WatchLogoutRequestDto;
import com.fluxion.sote.watch.dto.WatchLogoutResponseDto;
import com.fluxion.sote.watch.service.WatchLogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchLogoutServiceImpl implements WatchLogoutService {

    private final UserRepository userRepository;
    // 필요하다면 RefreshTokenRepository, DeviceRepository 등을 주입해서 사용

    @Override
    @Transactional
    public WatchLogoutResponseDto.LogoutResult logoutFromWatch(
            Long userId,
            WatchLogoutRequestDto.Logout request
    ) {
        User user = findUser(userId);

        String deviceId = request != null ? request.getDeviceId() : null;

        // TODO: 여기서 실제로
        //  1) deviceId 기반으로 워치용 리프레시 토큰 삭제
        //  2) 워치 세션 정보 정리
        // 같은 작업을 연결하면 됨

        return WatchLogoutResponseDto.LogoutResult.builder()
                .message("WATCH_LOGOUT_SUCCESS")
                .build();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다. id=" + userId));
    }
}
