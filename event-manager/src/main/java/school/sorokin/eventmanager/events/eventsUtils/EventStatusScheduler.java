package school.sorokin.eventmanager.events.eventsUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventcommon.kafka.ChangeItem;
import school.sorokin.eventcommon.kafka.EventChangeMessage;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.EventStatus;
import school.sorokin.eventmanager.events.repository.EventRepository;
import school.sorokin.eventmanager.events.repository.RegistrationRepository;
import school.sorokin.eventmanager.kafka.EventChangeProducer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventStatusScheduler {

    private final EventRepository eventRepository;
    private final EventChangeProducer eventChangeProducer;
    private final RegistrationRepository registrationRepository;

    @Scheduled(cron = "${scheduler.cron}")
    @Transactional
    public void updateEventStatus() {
        log.info("=== ШЕДУЛЕР ЗАПУЩЕН ===");
        LocalDateTime now = LocalDateTime.now();
        int started = eventRepository.updateStatusToStarted(now);
        log.info("Запущено мероприятий: {}", started);
        log.info("Отправка сообщения начала работы SCHEDULER в Kafka");
        sendKafkaEventStart();
        int finished = eventRepository.updateStatusToFinished(now);
        log.info("Завершено мероприятий: {}", finished);
        log.info("Отправка сообщения завершения SCHEDULER в Kafka");
        sendKafkaEventFinish();
        log.info("=== ШЕДУЛЕР ЗАВЕРШИЛ ===");
    }

    public void sendKafkaEventStart() {
        List<EventEntity> startedEvent = eventRepository.allStatEvent();
        for (EventEntity ev : startedEvent) {
            List<ChangeItem> changes = List.of(new ChangeItem(
                    "status",
                    EventStatus.WAIT_START,
                    EventStatus.STARTED
            ));
            EventChangeMessage newMessage = new EventChangeMessage(
                    UUID.randomUUID(),
                    "SCHEDULER_STATUS_CHANGE",
                    ev.getId(),
                    LocalDateTime.now(),
                    ev.getOwner().getId(),
                    null,
                    registrationRepository.findUserIdsByEventId(ev.getId()),
                    changes
            );
            eventChangeProducer.send(newMessage);
        }
    }
    public void sendKafkaEventFinish() {
        List<EventEntity> finishedEvent = eventRepository.allFinishEvent();
        for (EventEntity ev : finishedEvent) {
            List<ChangeItem> changes = List.of(new ChangeItem(
                    "status",
                    EventStatus.STARTED,
                    EventStatus.FINISHED
            ));
            EventChangeMessage newMessage = new EventChangeMessage(
                    UUID.randomUUID(),
                    "SCHEDULER_STATUS_CHANGE",
                    ev.getId(),
                    LocalDateTime.now(),
                    ev.getOwner().getId(),
                    null,
                    registrationRepository.findUserIdsByEventId(ev.getId()),
                    changes
            );
            eventChangeProducer.send(newMessage);
        }
    }
}
