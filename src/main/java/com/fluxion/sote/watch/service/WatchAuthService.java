package com.fluxion.sote.watch.service;

import com.fluxion.sote.auth.dto.TokenResponse;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.watch.dto.WatchPairCodeResponse;
import com.fluxion.sote.watch.dto.WatchPairLoginRequest;

public interface WatchAuthService {

    WatchPairCodeResponse createPairCodeForUser(User user);

    TokenResponse loginWithPairCode(WatchPairLoginRequest request);
}
