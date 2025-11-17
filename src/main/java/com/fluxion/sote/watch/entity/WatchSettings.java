// src/main/java/com/fluxion/sote/watch/entity/WatchSettings.java
package com.fluxion.sote.watch.entity;

import com.fluxion.sote.auth.entity.User;
// import com.fluxion.sote.global.model.entity.BaseEntity; // 있다면 사용
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "watch_settings")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watch_settings_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 알림 설정
    @Column(name = "notify_hrv", nullable = false)
    private boolean notifyHrv;

    @Column(name = "notify_health_sync", nullable = false)
    private boolean notifyHealthSync;

    @Column(name = "notify_diary", nullable = false)
    private boolean notifyDiary;

    @Column(name = "notify_challenge", nullable = false)
    private boolean notifyChallenge;

    // 데이터 전송 옵션
    @Column(name = "wifi_only", nullable = false)
    private boolean wifiOnly;

    @Column(name = "auto_sync", nullable = false)
    private boolean autoSync;

    public void updateNotifications(
            boolean notifyHrv,
            boolean notifyHealthSync,
            boolean notifyDiary,
            boolean notifyChallenge
    ) {
        this.notifyHrv = notifyHrv;
        this.notifyHealthSync = notifyHealthSync;
        this.notifyDiary = notifyDiary;
        this.notifyChallenge = notifyChallenge;
    }

    public void updateSyncOptions(
            boolean wifiOnly,
            boolean autoSync
    ) {
        this.wifiOnly = wifiOnly;
        this.autoSync = autoSync;
    }

    public void updateAll(
            boolean notifyHrv,
            boolean notifyHealthSync,
            boolean notifyDiary,
            boolean notifyChallenge,
            boolean wifiOnly,
            boolean autoSync
    ) {
        this.notifyHrv = notifyHrv;
        this.notifyHealthSync = notifyHealthSync;
        this.notifyDiary = notifyDiary;
        this.notifyChallenge = notifyChallenge;
        this.wifiOnly = wifiOnly;
        this.autoSync = autoSync;
    }
}
