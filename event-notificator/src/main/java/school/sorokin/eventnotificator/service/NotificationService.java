package school.sorokin.eventnotificator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import school.sorokin.eventcommon.kafka.EventChangeMessage;
import school.sorokin.eventnotificator.dto.NotificationResponseDto;
import school.sorokin.eventnotificator.entity.NotificationEntity;
import school.sorokin.eventnotificator.entity.NotificationEventPayloadEntity;
import school.sorokin.eventnotificator.repository.NotificationEventPayloadRepository;
import school.sorokin.eventnotificator.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationEventPayloadRepository notificationEventPayloadRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;


    public List<NotificationResponseDto> getNotReadNotificationForUser() throws JsonProcessingException {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<NotificationEntity> allNotification = notificationRepository.findByUserIdAndIsReadFalse(userId);
        if (allNotification.isEmpty()) {
            throw new RuntimeException("Список уведомлений пуст");
        }
        List<NotificationResponseDto> result = new ArrayList<>();
        for (NotificationEntity n : allNotification) {
            NotificationEventPayloadEntity notificationEventPayloadEntity =
                    notificationEventPayloadRepository.findByPayloadId(n.getPayLoadId());
            EventChangeMessage eventChangeMessage = objectMapper.readValue(notificationEventPayloadEntity.getPayload(), EventChangeMessage.class);
            NotificationResponseDto notificationResponseDto = new NotificationResponseDto(
                    n.getNotificationId(),
                    notificationEventPayloadEntity.getEventType(),
                    notificationEventPayloadEntity.getEventId(),
                    n.getCreatedAt(),
                    n.getIsRead(),
                    "Мероприятие было изменено:",
                    eventChangeMessage.changes()
            );
            result.add(notificationResponseDto);
        }
        return result;
    }

    public void readAllNotification() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int updated = notificationRepository.markAllAsRead(userId);
        log.info("Помечено как прочитанное {} уведомлений", updated);
    }
}
