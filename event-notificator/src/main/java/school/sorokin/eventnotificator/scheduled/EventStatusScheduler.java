package school.sorokin.eventnotificator.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import school.sorokin.eventcommon.kafka.ChangeItem;
import school.sorokin.eventcommon.kafka.EventChangeMessage;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.EventStatus;
import school.sorokin.eventmanager.events.repository.EventRepository;
import school.sorokin.eventmanager.events.repository.RegistrationRepository;
import school.sorokin.eventmanager.kafka.EventChangeProducer;
import school.sorokin.eventnotificator.repository.NotificationEventPayloadRepository;
import school.sorokin.eventnotificator.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventStatusScheduler {
    private final NotificationRepository notificationRepository;
    private final NotificationEventPayloadRepository notificationEventPayloadRepository;

    @Scheduled(cron = "${scheduler.cron}")
    @Transactional
    public void updateEventStatus() {
        log.info("=== ШЕДУЛЕР ЗАПУЩЕН ===");
        LocalDateTime now = LocalDateTime.now();

        List<Long> toStartIds = eventRepository.findEventIdsToStart(now);
        List<Long> toFinishIds = eventRepository.findEventIdsToFinish(now);

        int started = eventRepository.updateStatusToStarted(now);
        int finished = eventRepository.updateStatusToFinished(now);

        log.info("Запущено мероприятий: {}, завершено: {}", started, finished);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        sendStatusChangeEvents(toStartIds, EventStatus.WAIT_START, EventStatus.STARTED);
                        sendStatusChangeEvents(toFinishIds, EventStatus.STARTED, EventStatus.FINISHED);
                    }
                }
        );
        log.info("=== ШЕДУЛЕР ЗАВЕРШИЛ ===");
    }

    private void sendStatusChangeEvents(List<Long> eventIds, EventStatus oldStatus, EventStatus newStatus) {
        if (eventIds.isEmpty()) {
            return;
        }

        for (Long eventId : eventIds) {
            EventEntity event = eventRepository.findById(eventId).orElse(null);
            if (event == null) continue;

            List<ChangeItem> changes = List.of(new ChangeItem("status", oldStatus, newStatus));
            EventChangeMessage message = new EventChangeMessage(
                    UUID.randomUUID(),
                    "SCHEDULER_STATUS_CHANGE",
                    eventId,
                    LocalDateTime.now(),
                    event.getOwner().getId(),
                    null,
                    registrationRepository.findUserIdsByEventId(eventId),
                    changes
            );
            eventChangeProducer.send(message);
        }
    }
}