package school.sorokin.eventnotificator.dto;

import school.sorokin.eventcommon.kafka.ChangeItem;

import java.time.LocalDateTime;
import java.util.List;

public record NotificationResponseDto(
        Long notificationId,
        String type,
        Long eventId,
        LocalDateTime createdAt,
        boolean isRead,
        String message,
        List<ChangeItem> payload
) {
}
