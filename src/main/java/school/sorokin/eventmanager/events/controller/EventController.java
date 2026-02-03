package school.sorokin.eventmanager.events.controller;

import org.springframework.boot.convert.PeriodFormat;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import school.sorokin.eventmanager.events.EventPagination;
import school.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import school.sorokin.eventmanager.events.dto.EventDto;
import school.sorokin.eventmanager.events.dto.EventUpdateRequestDto;
import school.sorokin.eventmanager.events.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;


    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public EventDto createEvent(EventCreateRequestDto eventCreateRequestDto) {
        return eventService.createEvent(eventCreateRequestDto);
    }

    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id);
    }

    @GetMapping("/{id}")
    public EventDto getEventById(@PathVariable Long id) {
        return eventService.findEventById(id);
    }

    @PutMapping
    public EventDto updateEvent(Long id, EventUpdateRequestDto eventUpdateRequestDto) {
        return eventService.updateEvent(id, eventUpdateRequestDto);
    }

    @PostMapping("/search")
    public Page<EventDto> getEventsByFilter(EventPagination pagination) {
        return eventService.getEventsByFilter(pagination);
    }

    @GetMapping("/my")
    public List<EventDto> getCreatedEventsOfTheCurrentUser() {
        return eventService.getCreatedEventsOfTheCurrentUser();
    }

    @PostMapping("/registrations/{id}")
    public String registerUserForTheEvent(@PathVariable Long id){
        return eventService.registrationUserForTheEvent(id);
    }

    @DeleteMapping("/registrations/cancel/{id}")
    public String unregisterUserForTheEvent(@PathVariable Long id){
        return eventService.cancelEvent(id);
    }

    @GetMapping("/registrations/my")
    public List<EventDto> getEventsOfTheCurrentUSer(){
        return eventService.getEventsOfTheCurrentUSer();
    }
}


