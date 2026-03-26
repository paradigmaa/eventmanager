package school.sorokin.eventmanager.events.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sorokin.eventmanager.events.dto.EventResponseDto;
import school.sorokin.eventmanager.events.dto.RegistrationResponseDto;
import school.sorokin.eventmanager.events.entity.RegistrationEntity;
import school.sorokin.eventmanager.events.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
public class RegistrationController {

    private final static Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    public RegistrationController(EventService eventService) {

        this.eventService = eventService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<RegistrationResponseDto> registerUserForTheEvent(@PathVariable Long id) {
        log.info("POST /events/registrations/{} - Регистрация пользователя на мероприятие", id);
        RegistrationResponseDto dto = eventService.registrationUserForTheEvent(id);
        log.info("Пользователь зарегистрирован на мероприятие ID: {}", id);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> unregisterUserForTheEvent(@PathVariable Long id) {
        log.info("DELETE /events/registrations/cancel/{} - Отмена регистрации на мероприятие", id);
        eventService.cancelEvent(id);
        log.info("Регистрация отменена для мероприятия ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDto>> getEventsOfTheCurrentUser() {
        log.debug("GET /events/registrations/my - Получение мероприятий на которые зарегистрирован пользователь");
        List<EventResponseDto> response = eventService.getEventsOfTheCurrentUser();
        log.debug("Найдено {} мероприятий для участия", response.size());
        return ResponseEntity.ok(response);
    }
}
