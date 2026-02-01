package school.sorokin.eventmanager.events.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import school.sorokin.eventmanager.events.service.EventService;

@Controller
@RequestMapping("/registration/${id}")
public class EventRegistration {

    private final EventService eventService;

    public EventRegistration(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping()
    public String registrationUserForTheEvent(@PathVariable Long id){
        return eventService.registrationUser(id);
    }
}
