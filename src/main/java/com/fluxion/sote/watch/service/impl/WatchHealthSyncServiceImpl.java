// src/main/java/com/fluxion/sote/watch/service/impl/WatchHealthSyncServiceImpl.java
package com.fluxion.sote.watch.service.impl;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.user.repository.UserRepository;
import com.fluxion.sote.watch.dto.WatchHealthSyncResponseDto;
import com.fluxion.sote.watch.service.WatchHealthSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchHealthSyncServiceImpl implements WatchHealthSyncService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public WatchHealthSyncResponseDto.ForceSyncResult forceSync(Long userId) {
        User user = findUser(userId);

        LocalDateTime now = LocalDateTime.now();

        // TODO: 여기서 실제로
        //  1) 워치/Health Connect/서버에 있는 최신 건강 데이터 읽고
        //  2) HRV/심박수/걸음수 등 저장하는 로직을 연결하면 됨

        return WatchHealthSyncResponseDto.ForceSyncResult.builder()
                .status("SUCCESS")
                .syncType("MANUAL")
                .syncedAt(now.toString())
                .build();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다. id=" + userId));
    }
}
