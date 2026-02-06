package school.sorokin.eventmanager.events;

import org.springframework.stereotype.Component;
import school.sorokin.eventmanager.events.domain.Event;
import school.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import school.sorokin.eventmanager.events.dto.EventResponseDto;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.EventStatus;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.entity.UserEntity;
import java.util.ArrayList;

@Component
public class EventConverter {

    public Event convertEventCreateRequestDtoToEvent(EventCreateRequestDto eventCreateRequestDto,
                                                     LocationEntity location, UserEntity owner) {
        return new Event(
                null,
                eventCreateRequestDto.name(),
                eventCreateRequestDto.date(),
                eventCreateRequestDto.cost(),
                eventCreateRequestDto.duration(),
                eventCreateRequestDto.maxPlaces(),
                owner,
                location,
                new ArrayList<>(),
                null
        );
    }


    public EventEntity convertEventToEventEntity(Event domainEvent) {
        return new EventEntity(
                null,
                domainEvent.name(),
                domainEvent.dateTime(),
                domainEvent.cost(),
                domainEvent.duration(),
                domainEvent.maxPlaces(),
                domainEvent.owner(),
                domainEvent.location(),
                new ArrayList<>(),
                EventStatus.WAIT_START
        );

    }

    public Event convertEvenEntityToEvent(EventEntity eventEntity) {
        return new Event(
                eventEntity.getId(),
                eventEntity.getName(),
                eventEntity.getDateTime(),
                eventEntity.getCost(),
                eventEntity.getDuration(),
                eventEntity.getMaxPlaces(),
                eventEntity.getOwner(),
                eventEntity.getLocation(),
                eventEntity.getRegistrations(),
                eventEntity.getStatus()
        );

    }


    public EventResponseDto convertEvenEntityToEvenDto(EventEntity entityEvent) {
        return new EventResponseDto(
                entityEvent.getId(),
                entityEvent.getName(),
                entityEvent.getOwner().getId(),
                entityEvent.getMaxPlaces(),
                entityEvent.getRegistrations().size(),
                entityEvent.getDateTime(),
                entityEvent.getCost(),
                entityEvent.getDuration(),
                entityEvent.getLocation().getId(),
                entityEvent.getStatus()
        );

    }
}
