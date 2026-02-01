package school.sorokin.eventmanager.events;

import org.springframework.stereotype.Component;
import school.sorokin.eventmanager.events.domain.Event;
import school.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import school.sorokin.eventmanager.events.dto.EventDto;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.EventStatus;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.entity.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class EventConverter {

    public Event convertEventCreateRequestDtoToEvent(EventCreateRequestDto eventCreateRequestDto) {
        return new Event(
                eventCreateRequestDto.name(),
                eventCreateRequestDto.maxPlaces(),
                eventCreateRequestDto.date(),
                eventCreateRequestDto.cost(),
                eventCreateRequestDto.duration(),
                eventCreateRequestDto.locationId()
        );
    }


    public EventEntity convertEventToEventEntity(UserEntity owner, LocationEntity location, Event domainEvent) {
        return new EventEntity(
                null,
                domainEvent.name(),
                domainEvent.date(),
                domainEvent.cost(),
                domainEvent.duration(),
                domainEvent.maxPlaces(),
                owner,
                location,
                new ArrayList<>(),
                EventStatus.WAIT_START
        );

    }

    public EventDto convertEvenEntityToEvenDto(EventEntity entityEvent) {
        return new EventDto(
                entityEvent.getId(),
                entityEvent.getName(),
                entityEvent.getOwner().getId(),
                entityEvent.getMaxPlaces(),
                entityEvent.getRegistrations().size(),
                entityEvent.getDate(),
                entityEvent. getCost(),
                entityEvent.getDuration(),
                entityEvent.getLocation().getId(),
                entityEvent.getStatus()
        );

    }
}
