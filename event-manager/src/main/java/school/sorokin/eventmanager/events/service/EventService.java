package school.sorokin.eventmanager.events.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventcommon.kafka.EventChangeMessage;
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
import school.sorokin.eventmanager.kafka.EventChangeProducer;
import school.sorokin.eventmanager.kafka.EventComparator;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.dto.RoleUsers;
import school.sorokin.eventmanager.users.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EventConverter eventConverter;
    private final EventUserCheckService eventUserCheckService;
    private final EventLocationCheckService eventLocationCheckService;
    private final EventComparator eventComparator;
    private final EventChangeProducer eventChangeProducer;
    private final static Logger log = LoggerFactory.getLogger(EventService.class);

    public EventService(EventRepository eventRepository, RegistrationRepository registrationRepository, EventConverter eventConverter, EventUserCheckService eventUserCheckService, EventLocationCheckService eventLocationCheckService, EventComparator eventComparator, EventChangeProducer eventChangeProducer) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.eventConverter = eventConverter;
        this.eventUserCheckService = eventUserCheckService;
        this.eventLocationCheckService = eventLocationCheckService;
        this.eventComparator = eventComparator;
        this.eventChangeProducer = eventChangeProducer;
    }

    @Transactional
    public EventResponseDto createEvent(EventCreateRequestDto eventCreateRequestDto) {
        log.info("Запрос на создание мероприятия");
        return createEventHelper(eventCreateRequestDto);
    }

    @Transactional
    public String deleteEvent(Long id) {
        log.info("Запрос на удаление мероприятия по id={}", id);
        UserEntity owner = eventUserCheckService.checkToFindUser();
        EventEntity eventEntity = checkFindEventByIdHelper(id);
        if (!owner.getId().equals(eventEntity.getOwner().getId()) && !owner.getRole().equals(RoleUsers.ADMIN)) {
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
        UserEntity owner = eventUserCheckService.checkToFindUser();
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
        LocationEntity location = eventLocationCheckService.checkLocationId(eventCreateRequestDto.locationId());
        UserEntity owner = eventUserCheckService.checkToFindUser();
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


    private EventResponseDto updateEventHelper(Long id, EventUpdateRequestDto requestDto) {
        EventEntity oldEntity = checkFindEventByIdHelper(id);
        UserEntity currentUser = eventUserCheckService.checkToFindUser();
        int registeredCount = registrationRepository.countByEvent(oldEntity);
        if(eventRepository.existsByName(requestDto.name())){
            throw new AlreadyEventNameExistException("Мероприятие с таким именем уже существует");
        }
        if (!currentUser.getId().equals(oldEntity.getOwner().getId()) && !currentUser.getRole().equals(RoleUsers.ADMIN)) {
            throw new EventDeleteException("Мероприятие может обновлять только владелец или администратор");
        }
        if (requestDto.maxPlaces() < registeredCount) {
            log.error("Нельзя уменьшить места до {} при {} зарегистрированных", requestDto.maxPlaces(), registeredCount);
            throw new EventFullException(
                    "Нельзя уменьшить количество мест до %d, так как уже зарегистрировано %d участников"
                            .formatted(requestDto.maxPlaces(), registeredCount)
            );
        }
        if (requestDto.maxPlaces() > oldEntity.getMaxPlaces()) {
            eventLocationCheckService.checkLocationCapacity(eventLocationCheckService.checkLocationId(oldEntity.getLocation().getId()), requestDto.maxPlaces());
        }
        LocationEntity location = resolveLocation(oldEntity, requestDto);
        EventEntity updatedEntity = new EventEntity(
                oldEntity.getId(),
                requestDto.name(),
                requestDto.date(),
                requestDto.cost(),
                requestDto.duration(),
                requestDto.maxPlaces(),
                oldEntity.getOwner(),
                location,
                oldEntity.getRegistrations(),
                oldEntity.getStatus()
        );
        log.info("Сохранение обновленного мероприятия ID: {}", oldEntity.getId());
        EventEntity newEntity = eventRepository.save(updatedEntity);
        List<Long>subscribes = registrationRepository.findUserIdsByEventId(newEntity.getId());
        EventChangeMessage eventChangeMessage = new EventChangeMessage(
                UUID.randomUUID(),
                "UPDATE",
                newEntity.getId(),
                LocalDateTime.now(),
                currentUser.getId(),
                currentUser.getId(),
                subscribes,
                eventComparator.compareToEvent(oldEntity,newEntity)
        );
        eventChangeProducer.send(eventChangeMessage);
        return eventConverter.convertEvenEntityToEvenDto(newEntity);
    }

    private LocationEntity resolveLocation(EventEntity event, EventUpdateRequestDto dto) {
        if (dto.locationId().equals(event.getLocation().getId())) {
            return eventLocationCheckService.checkLocationId(event.getLocation().getId());
        }
        LocationEntity newLocation = eventLocationCheckService.checkLocationId(dto.locationId());
        eventLocationCheckService.checkLocationCapacity(newLocation, dto.maxPlaces());
        return newLocation;
    }


    private RegistrationEntity registrationUserForTheEventHelper(Long id) {
        EventEntity eventEntity = findEventForRegistration(id);
        checkOnRegistration(eventEntity);
        UserEntity userIdRegistration = eventUserCheckService.checkToFindUser();
        checkAlreadyRegistration(eventEntity, userIdRegistration);
        RegistrationEntity registrationEntity = new RegistrationEntity(
                null,
                eventEntity,
                userIdRegistration
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
        UserEntity userIdRegistration = eventUserCheckService.checkToFindUser();
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
        UserEntity userIdRegistration = eventUserCheckService.checkToFindUser();
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
                                    event.getOwner().getId(),
                                    event.getMaxPlaces(),
                                    count.intValue(),
                                    event.getDateTime(),
                                    event.getCost(),
                                    event.getDuration(),
                                    event.getLocation().getId(),
                                    event.getStatus()
                            );
                        }
                ).toList();
    }
}

