package school.sorokin.eventnotificator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class  NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private Long userId;

    private Long payLoadId;

    private boolean isRead = false;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    public NotificationEntity(Long notificationId, Long userId, Long payLoadId, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.payLoadId = payLoadId;
        this.createdAt = createdAt;
    }
}
