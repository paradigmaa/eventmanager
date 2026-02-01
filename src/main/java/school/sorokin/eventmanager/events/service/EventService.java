package school.sorokin.eventmanager.events.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.events.EventConverter;
import school.sorokin.eventmanager.events.domain.Event;
import school.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import school.sorokin.eventmanager.events.dto.EventDto;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.RegistrationEntity;
import school.sorokin.eventmanager.events.exception.EventCapacityExceededException;
import school.sorokin.eventmanager.events.repository.EventRepository;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.entity.UserEntity;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventConverter eventConverter;
    private final EventServiceCheckerUserAndLocation eventServiceCheckerUserAndLocation;

    public EventService(EventRepository eventRepository, EventConverter eventConverter, EventServiceCheckerUserAndLocation eventServiceCheckerUserAndLocation) {
        this.eventRepository = eventRepository;
        this.eventConverter = eventConverter;
        this.eventServiceCheckerUserAndLocation = eventServiceCheckerUserAndLocation;
    }

    public EventDto createEvent(EventCreateRequestDto eventCreateRequestDto){
        LocationEntity location = eventServiceCheckerUserAndLocation.checkLocationId(eventCreateRequestDto.locationId());
        UserEntity owner = eventServiceCheckerUserAndLocation.checkToFindUser();
        Event domainEvent = eventConverter.convertEventCreateRequestDtoToEvent(eventCreateRequestDto);
        if (location.getCapacity() < domainEvent.maxPlaces()) {
            int deficit = domainEvent.maxPlaces() - location.getCapacity();
            throw new EventCapacityExceededException(location.getName(), location.getCapacity(), domainEvent.maxPlaces(), deficit);
        }

        EventEntity entityEvent = eventRepository.save(eventConverter.convertEventToEventEntity(owner, location, domainEvent));

        return eventConverter.convertEvenEntityToEvenDto(entityEvent);
    }

    public String registrationUser(Long id) {
        UserEntity user = eventServiceCheckerUserAndLocation.checkToFindUser();

    }
}
