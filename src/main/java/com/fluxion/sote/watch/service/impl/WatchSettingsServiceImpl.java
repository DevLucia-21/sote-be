// src/main/java/com/fluxion/sote/watch/service/impl/WatchSettingsServiceImpl.java
package com.fluxion.sote.watch.service.impl;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.user.repository.UserRepository;
import com.fluxion.sote.watch.converter.WatchSettingsConverter;
import com.fluxion.sote.watch.dto.WatchSettingsRequestDto;
import com.fluxion.sote.watch.dto.WatchSettingsResponseDto;
import com.fluxion.sote.watch.entity.WatchSettings;
import com.fluxion.sote.watch.repository.WatchSettingsRepository;
import com.fluxion.sote.watch.service.WatchSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchSettingsServiceImpl implements WatchSettingsService {

    private final UserRepository userRepository;
    private final WatchSettingsRepository watchSettingsRepository;

    @Override
    public WatchSettingsResponseDto.SettingsResult getMySettings(Long userId) {
        User user = findUser(userId);

        return watchSettingsRepository.findByUser(user)
                .map(WatchSettingsConverter::toSettingsResult)
                .orElseGet(WatchSettingsConverter::defaultSettings);
    }

    @Override
    @Transactional
    public WatchSettingsResponseDto.SettingsResult updateMySettings(
            Long userId,
            WatchSettingsRequestDto.UpdateSettings request
    ) {
        User user = findUser(userId);

        WatchSettings settings = watchSettingsRepository.findByUser(user)
                .orElseGet(() -> WatchSettingsConverter.toEntity(user, request));

        if (settings.getId() == null) {
            watchSettingsRepository.save(settings);
        } else {
            WatchSettingsConverter.applyUpdate(settings, request);
        }

        return WatchSettingsConverter.toSettingsResult(settings);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다. id=" + userId));
    }
}
