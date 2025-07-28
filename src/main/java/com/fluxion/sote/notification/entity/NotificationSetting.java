package com.fluxion.sote.notification.entity;

import com.fluxion.sote.notification.enums.NotificationType;
import com.fluxion.sote.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public NotificationSetting(User user, NotificationType notificationType) {
        this.user = user;
        this.notificationType = notificationType;
    }
}
