package school.sorokin.eventnotificator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventcommon.kafka.EventChangeMessage;
import school.sorokin.eventnotificator.entity.NotificationEntity;
import school.sorokin.eventnotificator.entity.NotificationEventPayloadEntity;
import school.sorokin.eventnotificator.repository.NotificationEventPayloadRepository;
import school.sorokin.eventnotificator.repository.NotificationRepository;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProcessor {
    private final NotificationEventPayloadRepository notificationEventPayloadRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processor(EventChangeMessage eventChangeMessage) {
        NotificationEventPayloadEntity newPayload = new NotificationEventPayloadEntity(
                null,
                eventChangeMessage.messageId(),
                eventChangeMessage.eventType(),
                eventChangeMessage.eventId(),
                eventChangeMessage.occurredAt(),
                eventChangeMessage.changedById(),
                eventChangeMessage.ownerId(),
                toJson(eventChangeMessage)
        );
        notificationEventPayloadRepository.save(newPayload);

        for (Long sub : eventChangeMessage.subscribers()) {
            NotificationEntity notificationEntity = new NotificationEntity(
                    null,
                    sub,
                    newPayload.getPayloadId(),
                    LocalDateTime.now()
            );
            notificationRepository.save(notificationEntity);
        }
    }

    private String toJson(EventChangeMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации", e);
        }
    }
}
