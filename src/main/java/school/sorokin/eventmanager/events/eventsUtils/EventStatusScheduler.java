package school.sorokin.eventmanager.events.eventsUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventmanager.events.repository.EventRepository;

import java.time.LocalDateTime;


@Service
public class EventStatusScheduler {
    private final static Logger log = LoggerFactory.getLogger(EventStatusScheduler.class);

    private final EventRepository eventRepository;


    public EventStatusScheduler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "${scheduler.cron}")
    @Transactional
    public void updateEventStatus() {
        log.info("=== ШЕДУЛЕР ЗАПУЩЕН ===");
        LocalDateTime now = LocalDateTime.now();
        int started = eventRepository.updateStatusToStarted(now);
        log.info("Запущено мероприятий: {}", started);
        int finished = eventRepository.updateStatusToFinished(now);
        log.info("Завершено мероприятий: {}", finished);
        log.info("=== ШЕДУЛЕР ЗАВЕРШИЛ ===");
    }
}
