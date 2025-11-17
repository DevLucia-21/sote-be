package com.fluxion.sote.watch.controller;

import com.fluxion.sote.watch.dto.WatchNotificationDtos.RegisterTokenRequest;
import com.fluxion.sote.watch.dto.WatchNotificationDtos.SendTestNotificationRequest;
import com.fluxion.sote.watch.service.WatchNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/watch/notification")
public class WatchNotificationController {

    private final WatchNotificationService watchNotificationService;

    public WatchNotificationController(WatchNotificationService watchNotificationService) {
        this.watchNotificationService = watchNotificationService;
    }

    @PostMapping("/token")
    public ResponseEntity<Void> registerToken(@RequestBody RegisterTokenRequest request) {
        watchNotificationService.registerToken(request.getDeviceId(), request.getFcmToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test")
    public ResponseEntity<Void> sendTest(@RequestBody SendTestNotificationRequest request) {
        watchNotificationService.sendNotificationToCurrentUser(
                request.getTitle(),
                request.getBody(),
                Map.of("type", "TEST")
        );
        return ResponseEntity.ok().build();
    }
}
