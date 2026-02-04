package school.sorokin.eventmanager.events.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventmanager.events.eventsUtils.EventPagination;
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
        if (!owner.getId().equals(eventEntity.getOwner().getId()) && !owner.getRole().equals("ADMIN")) {
            log.warn("Попытка удаления мероприятия не владельцем или админом");
            throw new EventDeleteException("Мероприятие может удалить только владелец или администратор");
        }
        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            log.debug("Попытка удаления мероприятия, когда оно уже началось");
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
        log.debug("Найдено {} мероприятий", pageResult.getTotalElements());
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
    public String registrationUserForTheEvent(Long id) {
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
        if (location.getCapacity() < domainEvent.maxPlaces()) {
            int deficit = domainEvent.maxPlaces() - location.getCapacity();
            log.error("Превышение capacity локации: {} < {}", location.getCapacity(), domainEvent.maxPlaces());
            throw new EventCapacityExceededException(location.getName(), location.getCapacity(), domainEvent.maxPlaces(), deficit);
        }
        if (domainEvent.dateTime().isBefore(java.time.LocalDateTime.now())) {
            log.error("Дата мероприятия в прошлом: {}", domainEvent.dateTime());
            throw new EventDateException("Дата мероприятия не может быть в прошлом");
        }
        EventEntity entityEvent = eventRepository.save(eventConverter.convertEventToEventEntity(domainEvent));
        log.info("Мероприятие создано с ID: {}", entityEvent.getId());
        return eventConverter.convertEvenEntityToEvenDto(entityEvent);
    }

    private EventEntity checkFindEventByIdHelper(Long id) {
        EventEntity findEntity = eventRepository.findById(id)
                .orElseThrow(
                        () -> {
                            log.warn("Мероприятие с ID={} не найдено", id);
                            return new EventNotFoundException("Мероприятие не найдено");
                        });
        return findEntity;
    }

    private EventResponseDto updateEventHelper(Long id, EventUpdateRequestDto eventUpdateRequestDto) {
        log.info("Запрос на изменение мероприятия по ID={}", id);
        EventEntity foundEntity = checkFindEventByIdHelper(id);
        log.info("Проверка мероприятия и прав пользователя");
        UserEntity owner = eventServiceCheckerUser.checkToFindUser();
        if (!owner.getId().equals(foundEntity.getOwner().getId()) && !owner.getRole().equals("ADMIN")) {
            log.warn("Пользователь {} пытался обновить чужое мероприятие ID: {}", owner.getLogin(), id);
            throw new EventDeleteException("Мероприятие может обновлять только владелец или администратор");
        }
        if (!foundEntity.getStatus().equals(EventStatus.WAIT_START)) {
            log.warn("Попытка обновить мероприятие со статусом {}", foundEntity.getStatus());
            throw new EventStatusException("Нельзя редактировать мероприятие, которое уже началось или завершено");
        }

        if (eventUpdateRequestDto.maxPlaces() > foundEntity.getMaxPlaces()) {
            if (foundEntity.getLocation().getCapacity() < eventUpdateRequestDto.maxPlaces()) {
                int deficit = eventUpdateRequestDto.maxPlaces() - foundEntity.getLocation().getCapacity();
                log.error("Превышение capacity при обновлении: {} < {}", foundEntity.getLocation().getCapacity(), eventUpdateRequestDto.maxPlaces());
                throw new EventCapacityExceededException(
                        foundEntity.getLocation().getName(),
                        foundEntity.getLocation().getCapacity(),
                        eventUpdateRequestDto.maxPlaces(),
                        deficit
                );
            }
        }
        int registeredCount = foundEntity.getRegistrations().size();
        if (eventUpdateRequestDto.maxPlaces() < registeredCount) {
            log.error("Нельзя уменьшить места до {} при {} зарегистрированных", eventUpdateRequestDto.maxPlaces(), registeredCount);
            throw new EventFullException(
                    "Нельзя уменьшить количество мест до %d, так как уже зарегистрировано %d участников"
                            .formatted(eventUpdateRequestDto.maxPlaces(), registeredCount)
            );
        }
        if (eventUpdateRequestDto.dateTime().isBefore(java.time.LocalDateTime.now())) {
            log.error("Дата мероприятия установлена в прошлом: {}", eventUpdateRequestDto.dateTime());
            throw new EventDateException("Дата мероприятия не может быть в прошлом");
        }


        EventEntity updateEntity = new EventEntity(
                foundEntity.getId(),
                eventUpdateRequestDto.name(),
                eventUpdateRequestDto.dateTime(),
                eventUpdateRequestDto.cost(),
                eventUpdateRequestDto.duration(),
                eventUpdateRequestDto.maxPlaces(),
                foundEntity.getOwner(),
                foundEntity.getLocation(),
                foundEntity.getRegistrations(),
                foundEntity.getStatus());
        EventEntity updatedEvent = eventRepository.save(updateEntity);
        log.info("Мероприятие ID: {} обновлено", id);
        return eventConverter.convertEvenEntityToEvenDto(updatedEvent);

    }

    private String registrationUserForTheEventHelper(Long id) {
        EventEntity eventEntity = eventRepository.findEventForRegistration(id)
                .orElseThrow(() -> {
                    log.warn("Мероприятие с ID: {} не найдено для регистрации", id);
                    return new EventNotFoundException("Мероприятие не найдено");
                });
        if (eventEntity.getStatus().equals(EventStatus.STARTED)) {
            log.warn("Попытка регистрации на начавшееся мероприятие ID: {}", id);
            throw new EventStatusException(("Мероприятие началось. Новые регистрации недоступны на мероприятие. " +
                    "Статус %s /'STARTED/'")
                    .formatted(eventEntity.getName()));
        }
        if (eventEntity.getStatus().equals(EventStatus.CANCELED)) {
            log.warn("Попытка регистрации на отмененное мероприятие ID: {}", id);
            throw new EventStatusException(("Мероприятие отменено. Новые регистрации недоступны на мероприятие. " +
                    "Статус %s /'CANCELED/'")
                    .formatted(eventEntity.getName()));
        }
        if (eventEntity.getStatus().equals(EventStatus.FINISHED)) {
            log.warn("Попытка регистрации на завершенное мероприятие ID: {}", id);
            throw new EventStatusException(("Мероприятие окончилось. Новые регистрации недоступны на мероприятие. " +
                    "Статус %s /'FINISHED/'")
                    .formatted(eventEntity.getName()));
        }
        if (eventEntity.getRegistrations().size() >= eventEntity.getMaxPlaces()) {
            log.warn("Мероприятие ID: {} переполнено: {}/{}", id, eventEntity.getRegistrations().size(), eventEntity.getMaxPlaces());
            throw new EventFullException("Мероприятие предусмотрено на %d мест, занято %d, регистрация невозможна"
                    .formatted(eventEntity.getMaxPlaces(), eventEntity.getRegistrations().size()));
        }
        UserEntity userIdRegistration = eventServiceCheckerUser.checkToFindUser();

        boolean alreadyRegister = eventEntity.getRegistrations().stream()
                .anyMatch(regUser -> regUser.getUser().getId().equals(userIdRegistration.getId()));
        if (alreadyRegister) {
            log.warn("Пользователь {} уже зарегистрирован на мероприятие ID: {}", userIdRegistration.getLogin(), id);
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
        log.info("Пользователь {} зарегистрирован на мероприятие ID: {}", userIdRegistration.getLogin(), id);
        return "Успешная регистрация на мероприятие";
    }

    private String cancelEventHelper(Long id) {
        EventEntity eventEntity = eventRepository.findEventForRegistration(id)
                .orElseThrow(() -> {
                    log.warn("Мероприятие с ID: {} не найдено для отмены регистрации", id);
                    return new EventNotFoundException("Мероприятие не найдено");
                });
        UserEntity userIdRegistration = eventServiceCheckerUser.checkToFindUser();
        if (eventEntity.getStatus().equals(EventStatus.STARTED)) {
            log.warn("Попытка отмены регистрации с начавшегося мероприятия ID: {}", id);
            throw new EventStatusException(("Мероприятие началось. Невозможно отменить. " +
                    "Статус '%s'")
                    .formatted(eventEntity.getName()));
        }
        if (eventEntity.getStatus().equals(EventStatus.FINISHED)) {
            log.warn("Попытка отмены регистрации с завершенного мероприятия ID: {}", id);
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
            log.info("Регистрация пользователя {} отменена для мероприятия ID: {}", userIdRegistration.getLogin(), id);
            return "Заявка пользователя удалена";
        } else {
            log.warn("Пользователь {} не был зарегистрирован на мероприятие ID: {}", userIdRegistration.getLogin(), id);
            throw new RegistrationNotFoundException("У пользователя %s с id=%d нет такой регистрации"
                    .formatted(userIdRegistration.getLogin(), userIdRegistration.getId()));
        }
    }


    public List<EventResponseDto> getEventsOfTheCurrentUSer() {
        UserEntity userIdRegistration = eventServiceCheckerUser.checkToFindUser();
        log.debug("Поиск мероприятий для пользователя: {}", userIdRegistration.getLogin());
        List<Object[]> getAllEvents = eventRepository.getAllEventsCurrentUSer(userIdRegistration.getId());
        if (getAllEvents.isEmpty()) {
            log.debug("У пользователя {} не найдено мероприятий", userIdRegistration.getLogin());
            throw new EventNotFoundException("У пользователя не найдено мероприятий");
        }
        log.debug("Найдено {} мероприятий для пользователя", getAllEvents.size());
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

