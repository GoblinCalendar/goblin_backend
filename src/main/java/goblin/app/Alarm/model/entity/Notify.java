package goblin.app.Alarm.model.entity;

import goblin.app.User.model.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToMany
    private User user; // 유저 ID

    @Column(name = "fcm_token", nullable = false)
    private String fcmToken; // FCM 토큰

    @Column(name = "message", nullable = false)
    private String message; // 알림 메시지

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status; // 알림 전송 상태

    @Builder
    public Notification(NotificationStatus status, User user, String fcmToken, String message) {
        this.status = status;
        this.user = user;
        this.fcmToken = fcmToken;
        this.message = message;
    }
}

