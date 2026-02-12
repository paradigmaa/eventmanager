package school.sorokin.eventmanager.events.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sorokin.eventmanager.events.dto.EventPagination;
import school.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import school.sorokin.eventmanager.events.dto.EventResponseDto;
import school.sorokin.eventmanager.events.dto.EventUpdateRequestDto;
import school.sorokin.eventmanager.events.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final static Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;


    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventCreateRequestDto eventCreateRequestDto) {
        log.info("POST /events - Создание мероприятия: {}", eventCreateRequestDto.name());
        EventResponseDto response = eventService.createEvent(eventCreateRequestDto);
        log.info("Мероприятие создано с ID: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEventById(@PathVariable Long id) {
        log.info("DELETE /events/{} - Удаление мероприятия", id);
        String response = eventService.deleteEvent(id);
        log.info("Мероприятие ID: {} удалено", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        log.debug("GET /events/{} - Получение мероприятия", id);
        EventResponseDto response = eventService.findEventById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id,
                                                        @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        log.info("PUT /events/{} - Обновление мероприятия", id);
        EventResponseDto response = eventService.updateEvent(id, eventUpdateRequestDto);
        log.info("Мероприятие ID: {} обновлено", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<EventResponseDto>> getEventsByFilter(@RequestBody EventPagination pagination) {
        log.debug("POST /events/search - Поиск мероприятий с фильтрами");
        Page<EventResponseDto> response = eventService.getEventsByFilter(pagination);
        log.debug("Найдено {} мероприятий", response.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDto>> getUserEvents() {
        log.debug("GET /events/my - Получение мероприятий созданных текущим пользователем");
        List<EventResponseDto> response = eventService.getCreatedEventsOfTheCurrentUser();
        log.debug("Найдено {} созданных мероприятий", response.size());
        return ResponseEntity.ok(response);
    }

}


