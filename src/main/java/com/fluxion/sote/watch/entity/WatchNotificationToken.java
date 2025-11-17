package com.fluxion.sote.watch.entity;

import com.fluxion.sote.auth.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "watch_notification_tokens")
public class WatchNotificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 워치 페어링된 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_id", length = 128, nullable = false)
    private String deviceId; // 워치 고유 ID (네가 정한 값)

    @Column(name = "fcm_token", length = 512, nullable = false)
    private String fcmToken;

    @Column(name = "device_type", length = 32, nullable = false)
    private String deviceType; // 예: "WATCH"

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    protected WatchNotificationToken() {
    }

    public WatchNotificationToken(User user, String deviceId, String fcmToken, String deviceType) {
        this.user = user;
        this.deviceId = deviceId;
        this.fcmToken = fcmToken;
        this.deviceType = deviceType;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void updateToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
