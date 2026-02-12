package school.sorokin.eventmanager.events.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventmanager.events.dto.EventPagination;
import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.events.EventConverter;
import school.sorokin.eventmanager.events.domain.Event;
import school.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import school.sorokin.eventmanager.events.dto.EventResponseDto;
import school.sorokin.eventmanager.events.dto.EventUpdateRequestDto;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.EventStatus;
import school.sorokin.eventmanager.events.entity.RegistrationEntity;
import school.sorokin.eventmanager.events.exception.*;
import school.sorokin.eventmanager.events.repository.EventRepository;
import school.sorokin.eventmanager.events.repository.RegistrationRepository;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.dto.RoleUsers;
import school.sorokin.eventmanager.users.entity.UserEntity;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EventConverter eventConverter;
    private final EventServiceCheckerUser eventServiceCheckerUser;
    private final EventServiceCheckerLocation eventServiceCheckerLocation;
    private final static Logger log = LoggerFactory.getLogger(EventService.class);

    public EventService(EventRepository eventRepository, RegistrationRepository registrationRepository, EventConverter eventConverter, EventServiceCheckerUser eventServiceCheckerUser, EventServiceCheckerLocation eventServiceCheckerLocation) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.eventConverter = eventConverter;
        this.eventServiceCheckerUser = eventServiceCheckerUser;
        this.eventServiceCheckerLocation = eventServiceCheckerLocation;
    }

    @Transactional
    public EventResponseDto createEvent(EventCreateRequestDto eventCreateRequestDto) {
        log.info("Запрос на создание мероприятия");
        return createEventHelper(eventCreateRequestDto);
    }

    @Transactional
    public String deleteEvent(Long id) {
        log.info("Запрос на удаление мероприятия по id={}", id);
        UserEntity owner = eventServiceCheckerUser.checkToFindUser();
        EventEntity eventEntity = checkFindEventByIdHelper(id);
        if (!owner.getId().equals(eventEntity.getOwner()) && !owner.getRole().equals(RoleUsers.ADMIN)) {
            throw new EventDeleteException("Мероприятие может удалить только владелец или администратор");
        }
        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            throw new EventStatusException("Мероприятие уже началось, удаление невозможно");
        }
        eventEntity.setStatus(EventStatus.CANCELED);
        eventRepository.save(eventEntity);
        log.info("Мероприятие отменено владельцем или администратором - статус CANCELED");
        return "Мероприятие удалено";
    }

    @Transactional(readOnly = true)
    public EventResponseDto findEventById(Long id) {
        log.info("Поиск мероприятия ID: {}", id);
        return eventConverter.convertEvenEntityToEvenDto(checkFindEventByIdHelper(id));
    }

    @Transactional
    public EventResponseDto updateEvent(Long id, EventUpdateRequestDto eventUpdateRequestDto) {
        log.info("Обновление мероприятия ID: {}", id);
        return updateEventHelper(id, eventUpdateRequestDto);
    }

    @Transactional(readOnly = true)
    public Page<EventResponseDto> getEventsByFilter(EventPagination eventPagination) {
        log.debug("Поиск мероприятий по фильтру");
        int page = eventPagination.pageNumber();
        int size = eventPagination.pageSize();
        Pageable pageable = PageRequest.of(page, size);
        Page<EventEntity> pageResult = eventRepository.findEvents(
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
    public List<EventResponseDto> getCreatedEventsOfTheCurrentUser() {
        UserEntity owner = eventServiceCheckerUser.checkToFindUser();
        log.info("Поиск мероприятий созданных пользователем {} с ID={}", owner.getLogin(), owner.getId());
        List<EventEntity> eventsOwner = eventRepository.findByOwnerIdWithDetails(owner.getId());
        log.info("Найдено мероприятий {}", eventsOwner.size());
        return eventsOwner.stream().map(eventConverter::convertEvenEntityToEvenDto)
                .toList();
    }

    @Transactional
    public RegistrationEntity registrationUserForTheEvent(Long id) {
        log.info("Регистрация пользователя на мероприятие ID: {}", id);
        return registrationUserForTheEventHelper(id);
    }

    @Transactional
    public String cancelEvent(Long id) {
        log.info("Отмена регистрации на мероприятие ID: {}", id);
        return cancelEventHelper(id);

    }

    private EventResponseDto createEventHelper(EventCreateRequestDto eventCreateRequestDto) {
        LocationEntity location = eventServiceCheckerLocation.checkLocationId(eventCreateRequestDto.locationId());
        UserEntity owner = eventServiceCheckerUser.checkToFindUser();
        Event domainEvent = eventConverter.convertEventCreateRequestDtoToEvent(eventCreateRequestDto, location, owner);
        checkOnCreation(domainEvent, location);
        EventEntity entityEvent = eventRepository.save(eventConverter.convertEventToEventEntity(domainEvent));
        log.info("Мероприятие создано с ID: {}", entityEvent.getId());
        return eventConverter.convertEvenEntityToEvenDto(entityEvent);
    }

    private void checkOnCreation(Event domainEvent, LocationEntity location) {
        if (eventRepository.existsByName(domainEvent.name())) {
            throw new AlreadyEventNameExistException("Мероприятие с таким именем уже существует");
        }
        if (location.getCapacity() < domainEvent.maxPlaces()) {
            int deficit = domainEvent.maxPlaces() - location.getCapacity();
            throw new EventCapacityExceededException(location.getName(), location.getCapacity(), domainEvent.maxPlaces(), deficit);
        }
        if (domainEvent.dateTime().isBefore(java.time.LocalDateTime.now())) {
            throw new EventDateException("Дата мероприятия не может быть в прошлом");
        }
    }

    private EventEntity checkFindEventByIdHelper(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(
                        () -> new EventNotFoundException("Мероприятие не найдено"));
    }

    private EventResponseDto updateEventHelper(Long id, EventUpdateRequestDto dto) {
        EventEntity event = checkFindEventByIdHelper(id);
        UserEntity currentUser = eventServiceCheckerUser.checkToFindUser();
        if (!currentUser.getId().equals(event.getOwner()) && !currentUser.getRole().equals(RoleUsers.ADMIN)) {
            throw new EventDeleteException("Мероприятие может обновлять только владелец или администратор");
        }
        int registeredCount = registrationRepository.countByEvent(event);
        checkCapacityConstraintsForUpdate(event, dto, registeredCount);
        return performEventUpdate(event, dto);
    }

    private void checkCapacityConstraintsForUpdate(EventEntity event, EventUpdateRequestDto dto, int registeredCount) {
        if (dto.maxPlaces() < registeredCount) {
            log.error("Нельзя уменьшить места до {} при {} зарегистрированных", dto.maxPlaces(), registeredCount);
            throw new EventFullException(
                    "Нельзя уменьшить количество мест до %d, так как уже зарегистрировано %d участников"
                            .formatted(dto.maxPlaces(), registeredCount)
            );
        }

        if (dto.maxPlaces() > event.getMaxPlaces()) {
            checkLocationCapacity(eventServiceCheckerLocation.checkLocationId(event.getLocation()), dto.maxPlaces());
        }
    }

    private void checkLocationCapacity(LocationEntity location, int requestedPlaces) {
        if (location.getCapacity() < requestedPlaces) {
            int deficit = requestedPlaces - location.getCapacity();
            log.error("Превышение capacity: {} < {}", location.getCapacity(), requestedPlaces);
            throw new EventCapacityExceededException(
                    location.getName(),
                    location.getCapacity(),
                    requestedPlaces,
                    deficit
            );
        }
    }

    private EventResponseDto performEventUpdate(EventEntity event, EventUpdateRequestDto dto) {
        LocationEntity location = resolveLocation(event, dto);
        EventEntity updatedEntity = new EventEntity(
                event.getId(),
                dto.name(),
                dto.date(),
                dto.cost(),
                dto.duration(),
                dto.maxPlaces(),
                event.getOwner(),
                location.getId(),
                event.getRegistrations(),
                event.getStatus()
        );

        log.info("Сохранение обновленного мероприятия ID: {}", event.getId());
        EventEntity saved = eventRepository.save(updatedEntity);
        return eventConverter.convertEvenEntityToEvenDto(saved);
    }

    private LocationEntity resolveLocation(EventEntity event, EventUpdateRequestDto dto) {
        if (dto.locationId().equals(event.getLocation())) {
            return eventServiceCheckerLocation.checkLocationId(event.getLocation());
        }

        LocationEntity newLocation = eventServiceCheckerLocation.checkLocationId(dto.locationId());
        checkLocationCapacity(newLocation, dto.maxPlaces());
        return newLocation;
    }

    private RegistrationEntity registrationUserForTheEventHelper(Long id) {
        EventEntity eventEntity = findEventForRegistration(id);
        checkOnRegistration(eventEntity);
        UserEntity userIdRegistration = eventServiceCheckerUser.checkToFindUser();
        checkAlreadyRegistration(eventEntity, userIdRegistration);
        RegistrationEntity registrationEntity = new RegistrationEntity(
                null,
                eventEntity,
                userIdRegistration.getId()
        );
        eventEntity.getRegistrations().add(registrationEntity);
        return registrationRepository.save(registrationEntity);
    }

    private EventEntity findEventForRegistration(Long id) {
        return eventRepository.findEventForRegistration(id)
                .orElseThrow(() -> new EventNotFoundException("Мероприятие не найдено"));
    }

    private void checkOnRegistration(EventEntity eventEntity) {
        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            throw new EventStatusException(("Нельзя зарегистрироваться на мероприятие %s," +
                    " если оно уже начато или законченно")
                    .formatted(eventEntity.getName()));
        }
        if (eventEntity.getRegistrations().size() >= eventEntity.getMaxPlaces()) {
            throw new EventFullException("Мероприятие предусмотрено на %d мест, занято %d, регистрация невозможна"
                    .formatted(eventEntity.getMaxPlaces(), eventEntity.getRegistrations().size()));
        }
    }

    private void checkAlreadyRegistration(EventEntity eventEntity, UserEntity userIdRegistration) {
        boolean result = registrationRepository.alreadyRegister(eventEntity, userIdRegistration);
        if (result) {
            throw new AlreadyRegisterException("Пользователь %s с id=%d уже зарегистрирован на мероприятие /'%s/'"
                    .formatted(userIdRegistration.getLogin(), userIdRegistration.getId(), eventEntity.getName()));
        }
    }

    private String cancelEventHelper(Long id) {
        EventEntity eventEntity = eventRepository.findEventForRegistration(id)
                .orElseThrow(() -> {
                    log.warn("Мероприятие с ID: {} не найдено для отмены регистрации", id);
                    return new EventNotFoundException("Мероприятие не найдено");
                });
        UserEntity userIdRegistration = eventServiceCheckerUser.checkToFindUser();
        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            throw new EventStatusException(("Нельзя отменить регистрацию на мероприятие %s," +
                    " если оно уже начато или законченно")
                    .formatted(eventEntity.getName()));
        }
        Optional<RegistrationEntity> userRegistration = registrationRepository.findByEventAndUserId(eventEntity,
                userIdRegistration.getId());
        if (userRegistration.isPresent()) {
            eventEntity.getRegistrations().remove(userRegistration.get());
            registrationRepository.delete(userRegistration.get());
            return "Заявка пользователя удалена";
        } else {
            throw new RegistrationNotFoundException("У пользователя %s с id=%d нет такой регистрации"
                    .formatted(userIdRegistration.getLogin(), userIdRegistration.getId()));
        }
    }


    public List<EventResponseDto> getEventsOfTheCurrentUser() {
        UserEntity userIdRegistration = eventServiceCheckerUser.checkToFindUser();
        log.debug("Поиск мероприятий для пользователя: {}", userIdRegistration.getLogin());
        List<Object[]> getAllEvents = eventRepository.getAllEventsCurrentUser(userIdRegistration.getId());
        if (getAllEvents.isEmpty()) {
            log.debug("У пользователя {} не найдено мероприятий", userIdRegistration.getLogin());
            throw new EventNotFoundException("У пользователя не найдено мероприятий");
        }
        return getAllEvents.stream()
                .map(row -> {
                            EventEntity event = (EventEntity) row[0];
                            Long count = (Long) row[1];
                            return new EventResponseDto(
                                    event.getId(),
                                    event.getName(),
                                    event.getOwner(),
                                    event.getMaxPlaces(),
                                    count.intValue(),
                                    event.getDateTime(),
                                    event.getCost(),
                                    event.getDuration(),
                                    event.getLocation(),
                                    event.getStatus()
                            );
                        }
                ).toList();
    }
}

