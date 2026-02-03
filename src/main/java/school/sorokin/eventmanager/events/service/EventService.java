package school.sorokin.eventmanager.events.service;

import jakarta.persistence.Table;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventmanager.events.EventPagination;
import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.events.EventConverter;
import school.sorokin.eventmanager.events.domain.Event;
import school.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import school.sorokin.eventmanager.events.dto.EventDto;
import school.sorokin.eventmanager.events.dto.EventUpdateRequestDto;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.EventStatus;
import school.sorokin.eventmanager.events.entity.RegistrationEntity;
import school.sorokin.eventmanager.events.exception.*;
import school.sorokin.eventmanager.events.repository.EventRepository;
import school.sorokin.eventmanager.events.repository.RegistrationRepository;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EventConverter eventConverter;
    private final EventServiceCheckerUserAndLocation eventServiceCheckerUserAndLocation;

    public EventService(EventRepository eventRepository,
                        RegistrationRepository registrationRepository,
                        EventConverter eventConverter,
                        EventServiceCheckerUserAndLocation eventServiceCheckerUserAndLocation) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.eventConverter = eventConverter;
        this.eventServiceCheckerUserAndLocation = eventServiceCheckerUserAndLocation;
    }

    @Transactional
    public EventDto createEvent(EventCreateRequestDto eventCreateRequestDto) {
        return createEventHelper(eventCreateRequestDto);
    }

    @Transactional
    public String deleteEvent(Long id) {
        checkFindEventByIdHelper(id);
        eventRepository.deleteById(id);
        return "Мероприятие удалено";
    }

    @Transactional(readOnly = true)
    public EventDto findEventById(Long id) {
        return eventConverter.convertEvenEntityToEvenDto(checkFindEventByIdHelper(id));
    }

    @Transactional
    public EventDto updateEvent(Long id, EventUpdateRequestDto eventUpdateRequestDto) {
        return updateEventHelper(id, eventUpdateRequestDto);
    }

    @Transactional(readOnly = true)
    public Page<EventDto> getEventsByFilter(EventPagination eventPagination) {
        int page = eventPagination.pageNumber();
        int size = eventPagination.pageSize();
        Pageable pageable = PageRequest.of(page, size);
        Page<EventEntity> pageResult = eventRepository.searchEvents(
                eventPagination.name(),
                eventPagination.placesMin(),
                eventPagination.placesMax(),
                eventPagination.costMin(),
                eventPagination.costMax(),
                eventPagination.locationId(),
                eventPagination.eventStatus(),
                pageable
        );
        return pageResult.map(eventConverter::convertEvenEntityToEvenDto);

    }

    @Transactional(readOnly = true)
    public List<EventDto> getCreatedEventsOfTheCurrentUser() {
        UserEntity owner = eventServiceCheckerUserAndLocation.checkToFindUser();
        List<EventEntity> eventsOwner = eventRepository.findByOwnerIdWithDetails(owner.getId());
        return eventsOwner.stream().map(eventConverter::convertEvenEntityToEvenDto)
                .toList();
    }

    @Transactional
    public String registrationUserForTheEvent(Long id) {
        return registrationUserForTheEventHelper(id);
    }

    @Transactional
    public String cancelEvent(Long id) {
        return cancelEventHelper(id);

    }


    private EventDto createEventHelper(EventCreateRequestDto eventCreateRequestDto) {
        LocationEntity location = eventServiceCheckerUserAndLocation.checkLocationId(eventCreateRequestDto.locationId());
        UserEntity owner = eventServiceCheckerUserAndLocation.checkToFindUser();
        Event domainEvent = eventConverter.convertEventCreateRequestDtoToEvent(eventCreateRequestDto, location, owner);
        if (location.getCapacity() < domainEvent.maxPlaces()) {
            int deficit = domainEvent.maxPlaces() - location.getCapacity();
            throw new EventCapacityExceededException(location.getName(), location.getCapacity(), domainEvent.maxPlaces(), deficit);
        }
        EventEntity entityEvent = eventRepository.save(eventConverter.convertEventToEventEntity(domainEvent));
        return eventConverter.convertEvenEntityToEvenDto(entityEvent);
    }


    private EventEntity checkFindEventByIdHelper(Long id) {
        EventEntity findEntity = eventRepository.findById(id)
                .orElseThrow(
                        () -> new EventNotFoundException(id));
        return findEntity;
    }

    private EventDto updateEventHelper(Long id, EventUpdateRequestDto eventUpdateRequestDto) {
        EventEntity foundEntity = checkFindEventByIdHelper(id);
        EventEntity updateEntity = new EventEntity(
                foundEntity.getId(),
                eventUpdateRequestDto.name(),
                eventUpdateRequestDto.date(),
                eventUpdateRequestDto.cost(),
                eventUpdateRequestDto.duration(),
                eventUpdateRequestDto.maxPlaces(),
                foundEntity.getOwner(),
                foundEntity.getLocation(),
                foundEntity.getRegistrations(),
                foundEntity.getStatus());
        EventEntity updatedEvent = eventRepository.save(updateEntity);
        return eventConverter.convertEvenEntityToEvenDto(updatedEvent);

    }

    private String registrationUserForTheEventHelper(Long id) {
        EventEntity eventEntity = eventRepository.findEventForRegistration(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        if (eventEntity.getStatus().equals(EventStatus.STARTED)) {
            throw new EventStatusException(("Мероприятие началось. Новые регистрации недоступны на мероприятие. " +
                    "Статус %s /'STARTED/'")
                    .formatted(eventEntity.getName()));
        }
        if (eventEntity.getStatus().equals(EventStatus.CANCELED)) {
            throw new EventStatusException(("Мероприятие отменено. Новые регистрации недоступны на мероприятие. " +
                    "Статус %s /'CANCELED/'")
                    .formatted(eventEntity.getName()));
        }
        if (eventEntity.getStatus().equals(EventStatus.FINISHED)) {
            throw new EventStatusException(("Мероприятие окончилось. Новые регистрации недоступны на мероприятие. " +
                    "Статус %s /'FINISHED/'")
                    .formatted(eventEntity.getName()));
        }
        if (eventEntity.getRegistrations().size() >= eventEntity.getMaxPlaces()) {
            throw new EventFullException("Мероприятие предусмотрено на %d мест, занято %d, регистрация невозможна"
                    .formatted(eventEntity.getMaxPlaces(), eventEntity.getRegistrations().size()));
        }
        UserEntity userIdRegistration = eventServiceCheckerUserAndLocation.checkToFindUser();

        boolean alreadyRegister = eventEntity.getRegistrations().stream()
                .anyMatch(regUser -> regUser.getUser().getId().equals(userIdRegistration.getId()));
        if (alreadyRegister) {
            throw new AlreadyRegisterException("Пользователь %s с id=%d уже зарегистрирован на мероприятие /'%s/'"
                    .formatted(userIdRegistration.getLogin(), userIdRegistration.getId(), eventEntity.getName()));
        }
        RegistrationEntity registrationEntity = new RegistrationEntity(
                null,
                eventEntity,
                userIdRegistration
        );
        eventEntity.getRegistrations().add(registrationEntity);
        registrationRepository.save(registrationEntity);

        return "Успешная регистрация на мероприятие";
    }

    private String cancelEventHelper(Long id) {
        EventEntity eventEntity = eventRepository.findEventForRegistration(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        UserEntity userIdRegistration = eventServiceCheckerUserAndLocation.checkToFindUser();
        if (eventEntity.getStatus().equals(EventStatus.STARTED)) {
            throw new EventStatusException(("Мероприятие началось. Невозможно отменить. " +
                    "Статус '%s'")
                    .formatted(eventEntity.getName()));
        }
        if (eventEntity.getStatus().equals(EventStatus.FINISHED)) {
            throw new EventStatusException(("Мероприятие закончилось. Невозможно отменить. " +
                    "Статус '%s' ")
                    .formatted(eventEntity.getName()));
        }
        Optional<RegistrationEntity> userRegistration = eventEntity.getRegistrations().stream()
                .filter(reg -> reg.getUser().getId().equals(userIdRegistration.getId()))
                .findFirst();
        if (userRegistration.isPresent()) {
            eventEntity.getRegistrations().remove(userRegistration.get());
            registrationRepository.delete(userRegistration.get());
            return "Заявка пользователя удалена";
        } else {
            throw new RegistrationNotFoundException("У пользователя %s с id=%d нет такой регистрации"
                    .formatted(userIdRegistration.getLogin(), userIdRegistration.getId()));
        }
    }

    public List<EventDto> getEventsOfTheCurrentUSer() {
        UserEntity userIdRegistration = eventServiceCheckerUserAndLocation.checkToFindUser();
        List<EventEntity>
    }
}

