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
    public void updateEventStatus(){
        LocalDateTime now = LocalDateTime.now();
        updateEventsToStarted(now);
        updateEventsToFinished(now);
    }


    private void updateEventsToStarted(LocalDateTime now){
        List<EventEntity> eventsToStart = eventRepository.findByDateTimeBeforeAndStatus(now, EventStatus.WAIT_START);
        if(!eventsToStart.isEmpty()){
            eventsToStart.forEach(event ->
                    event.setStatus(EventStatus.STARTED));
        }
        eventRepository.saveAll(eventsToStart);
    }

    private void updateEventsToFinished(LocalDateTime now){
        List<EventEntity> startedEvents = eventRepository.findByStatus(EventStatus.STARTED);
        List<EventEntity> eventToFinish = startedEvents.stream()
                .filter(events -> {
                    LocalDateTime endTime = events.getDateTime()
                            .plusMinutes(events.getDuration());
                    return now.isAfter(endTime) || now.isEqual(endTime);
                })
                .toList();
        if(eventToFinish.isEmpty()){
            eventToFinish.forEach(events ->
                    events.setStatus(EventStatus.FINISHED));
        }
        eventRepository.saveAll(eventToFinish);
    }
}
