package com.fluxion.sote.setting.entity;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.setting.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "user_notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NotificationSetting {

    @EmbeddedId
    private Pk id;

    @MapsId("userId")  // 복합키의 userId를 User 매핑에 사용
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, insertable = false, updatable = false)
    private NotificationType notificationType;

    public NotificationSetting(User user, NotificationType notificationType) {
        this.user = user;
        this.notificationType = notificationType;
        this.id = new Pk(user.getId(), notificationType.name());
    }

    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pk implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "notification_type")
        private String notificationType;
    }
}
