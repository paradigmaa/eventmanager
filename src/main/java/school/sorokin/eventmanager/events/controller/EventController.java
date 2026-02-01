package school.sorokin.eventmanager.events.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import school.sorokin.eventmanager.events.dto.EventDto;
import school.sorokin.eventmanager.events.service.EventService;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;


    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

/*    @PostMapping
    public EventDto createEvent(EventCreateRequestDto eventCreateRequestDto){
        return eventService.createEvent(eventCreateRequestDto);
    }*/
}
