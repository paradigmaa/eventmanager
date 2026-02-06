package school.sorokin.eventmanager.events.eventsUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.EventStatus;
import school.sorokin.eventmanager.events.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class EventStatusScheduler {
    private final static Logger log = LoggerFactory.getLogger(EventStatusScheduler.class);

    private final EventRepository eventRepository;


    public EventStatusScheduler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateEventStatus() {
        log.info("=== ШЕДУЛЕР ЗАПУЩЕН В {} ===", LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();
        log.info("Текущее время для проверки: {}", now);

        updateEventsToStarted(now);
        updateEventsToFinished(now);

        log.info("=== ШЕДУЛЕР ЗАВЕРШИЛ РАБОТУ ===");
    }

    private void updateEventsToStarted(LocalDateTime now) {
        log.info("Поиск мероприятий для старта (now={}, status=WAIT_START)", now);
        List<EventEntity> eventsToStart = eventRepository.findByDateTimeBeforeAndStatus(now, EventStatus.WAIT_START);

        log.info("Найдено {} мероприятий для старта: {}",
                eventsToStart.size(),
                eventsToStart.stream().map(e -> e.getId() + ":" + e.getName()).toList());

        if (!eventsToStart.isEmpty()) {
            eventsToStart.forEach(event -> {
                log.info("Мероприятие ID={} '{}' меняет статус WAIT_START → STARTED",
                        event.getId(), event.getName());
                event.setStatus(EventStatus.STARTED);
            });
            eventRepository.saveAll(eventsToStart);
            log.info("Сохранено {} мероприятий со статусом STARTED", eventsToStart.size());
        }
    }

    private void updateEventsToFinished(LocalDateTime now) {
        log.info("Поиск мероприятий для завершения (status=STARTED)");
        List<EventEntity> startedEvents = eventRepository.findByStatus(EventStatus.STARTED);
        log.info("Всего мероприятий со статусом STARTED: {}", startedEvents.size());

        List<EventEntity> eventToFinish = startedEvents.stream()
                .filter(events -> {
                    LocalDateTime endTime = events.getDateTime()
                            .plusMinutes(events.getDuration());
                    boolean shouldFinish = now.isAfter(endTime) || now.isEqual(endTime);
                    if (shouldFinish) {
                        log.info("Мероприятие ID={} '{}' должно завершиться: endTime={}",
                                events.getId(), events.getName(), endTime);
                    }
                    return shouldFinish;
                })
                .toList();

        log.info("Найдено {} мероприятий для завершения", eventToFinish.size());

        if (!eventToFinish.isEmpty()) {
            eventToFinish.forEach(events -> {
                log.info("Мероприятие ID={} '{}' меняет статус STARTED → FINISHED",
                        events.getId(), events.getName());
                events.setStatus(EventStatus.FINISHED);
            });
            eventRepository.saveAll(eventToFinish);
            log.info("Сохранено {} мероприятий со статусом FINISHED", eventToFinish.size());
        }
    }
}
