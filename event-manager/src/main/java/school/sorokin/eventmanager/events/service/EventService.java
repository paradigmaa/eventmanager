package school.sorokin.eventmanager.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import school.sorokin.eventcommon.kafka.EventChangeMessage;
import school.sorokin.eventmanager.events.RegistrationConverter;
import school.sorokin.eventmanager.events.dto.*;
import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.events.EventConverter;
import school.sorokin.eventmanager.events.domain.Event;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.EventStatus;
import school.sorokin.eventmanager.events.entity.RegistrationEntity;
import school.sorokin.eventmanager.events.exception.*;
import school.sorokin.eventmanager.events.repository.EventRepository;
import school.sorokin.eventmanager.events.repository.RegistrationRepository;
import school.sorokin.eventmanager.kafka.EventChangeProducer;
import school.sorokin.eventmanager.events.EventComparator;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.dto.RoleUsers;
import school.sorokin.eventmanager.users.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EventConverter eventConverter;
    private final EventUserCheckService eventUserCheckService;
    private final EventLocationCheckService eventLocationCheckService;
    private final EventComparator eventComparator;
    private final EventChangeProducer eventChangeProducer;
    private final RegistrationConverter registrationConverter;

    @Transactional
    public EventResponseDto createEvent(EventCreateRequestDto eventCreateRequestDto) {
        log.info("Запрос на создание мероприятия");
        LocationEntity location = eventLocationCheckService.checkLocationId(eventCreateRequestDto.locationId());
        UserEntity owner = eventUserCheckService.checkToFindUser();
        Event domainEvent = eventConverter.convertEventCreateRequestDtoToEvent(eventCreateRequestDto, location, owner);
        eventLocationCheckService.checkLocationCapacity(location, domainEvent.maxPlaces());
        checkOnCreation(domainEvent);
        EventEntity entityEvent = eventRepository.save(eventConverter.convertEventToEventEntity(domainEvent));
        log.info("Мероприятие создано с ID: {}", entityEvent.getId());
        return eventConverter.convertEvenEntityToEvenDto(entityEvent);
    }

    private void checkOnCreation(Event domainEvent) {
        if (eventRepository.existsByName(domainEvent.name())) {
            throw new AlreadyEventNameExistException("Мероприятие с таким именем уже существует");
        }
        if (domainEvent.dateTime().isBefore(java.time.LocalDateTime.now())) {
            throw new EventDateException("Дата мероприятия не может быть в прошлом");
        }
    }

    @Transactional
    public String deleteEvent(Long id) {
        log.info("Запрос на удаление мероприятия по id={}", id);
        UserEntity owner = eventUserCheckService.checkToFindUser();
        EventEntity eventEntity = checkFindEventByIdHelper(id);
        checkOnDelete(owner, eventEntity);
        eventEntity.setStatus(EventStatus.CANCELED);
        eventRepository.save(eventEntity);
        log.info("Мероприятие отменено владельцем или администратором - статус CANCELED");
        return "Мероприятие удалено";
    }

    private void checkOnDelete(UserEntity owner, EventEntity eventEntity) {
        if (!owner.getId().equals(eventEntity.getOwner().getId()) && !owner.getRole().equals(RoleUsers.ADMIN)) {
            throw new EventDeleteException("Мероприятие может удалить только владелец или администратор");
        }
        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            throw new EventStatusException("Мероприятие уже началось, удаление невозможно");
        }
    }

    @Transactional(readOnly = true)
    public EventResponseDto findEventById(Long id) {
        log.info("Поиск мероприятия ID: {}", id);
        return eventConverter.convertEvenEntityToEvenDto(checkFindEventByIdHelper(id));
    }

    private EventEntity checkFindEventByIdHelper(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(
                        () -> new EventNotFoundException("Мероприятие не найдено"));
    }

    @Transactional
    public EventResponseDto updateEvent(Long id, EventUpdateRequestDto eventUpdateRequestDto) {
        log.info("Обновление мероприятия ID: {}", id);
        EventEntity oldEntity = checkFindEventByIdHelper(id);
        EventEntity oldEntityCopy = copyEntity(oldEntity);
        UserEntity currentUser = eventUserCheckService.checkToFindUser();
        int registrationCount = registrationRepository.countByEvent(oldEntity);
        checkOnUpdate(eventUpdateRequestDto, currentUser, oldEntity, registrationCount);
        LocationEntity newLocation = eventLocationCheckService.checkLocationId(eventUpdateRequestDto.locationId());
        eventLocationCheckService.checkLocationCapacity(newLocation, eventUpdateRequestDto.maxPlaces());
        EventEntity newEvent = new EventEntity(
                oldEntity.getId(),
                eventUpdateRequestDto.name(),
                eventUpdateRequestDto.date(),
                eventUpdateRequestDto.cost(),
                eventUpdateRequestDto.duration(),
                eventUpdateRequestDto.maxPlaces(),
                oldEntity.getOwner(),
                newLocation,
                oldEntity.getRegistrations(),
                oldEntity.getStatus()
        );
        EventEntity saveEvent = eventRepository.save(newEvent);
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        log.info("Транзакция закоммичена, отправляем в Kafka");
                        KafkaSendMessage(oldEntityCopy, saveEvent, currentUser);
                    }
                }
        );

        return eventConverter.convertEvenEntityToEvenDto(saveEvent);
    }

    private void checkOnUpdate(EventUpdateRequestDto requestDto,
                               UserEntity currentUser, EventEntity oldEntity,
                               int registeredCount) {
        if (!currentUser.getId().equals(oldEntity.getOwner().getId()) && !currentUser.getRole().equals(RoleUsers.ADMIN)) {
            throw new EventDeleteException("Мероприятие может обновлять только владелец или администратор");
        }
        if (!oldEntity.getName().equals(requestDto.name()) && eventRepository.existsByName(requestDto.name())) {
            throw new AlreadyEventNameExistException("Мероприятие с таким именем уже существует");
        }
        if (requestDto.maxPlaces() < registeredCount) {
            log.error("Нельзя уменьшить места до {} при {} зарегистрированных", requestDto.maxPlaces(), registeredCount);
            throw new EventFullException(
                    "Нельзя уменьшить количество мест до %d, так как уже зарегистрировано %d участников"
                            .formatted(requestDto.maxPlaces(), registeredCount)
            );
        }
    }
    private EventEntity copyEntity(EventEntity oldEntity){
        return  new EventEntity(
                oldEntity.getId(),
                oldEntity.getName(),
                oldEntity.getDateTime(),
                oldEntity.getCost(),
                oldEntity.getDuration(),
                oldEntity.getMaxPlaces(),
                oldEntity.getOwner(),
                oldEntity.getLocation(),
                oldEntity.getRegistrations(),
                oldEntity.getStatus()
        );
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
    public RegistrationResponseDto registrationUserForTheEvent(Long id) {
        log.info("Регистрация пользователя на мероприятие ID: {}", id);
        UserEntity userIdRegistration = eventUserCheckService.checkToFindUser();
        EventEntity eventEntity = checkFindEventByIdHelper(id);
        checkOnRegistration(eventEntity, userIdRegistration);
        RegistrationEntity registrationEntity = new RegistrationEntity(
                null,
                eventEntity,
                userIdRegistration
        );
        RegistrationEntity save = registrationRepository.save(registrationEntity);
        return registrationConverter.registrationToDto(save);
    }

    private void checkOnRegistration(EventEntity eventEntity, UserEntity userIdRegistration) {
        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            throw new EventStatusException(("Нельзя зарегистрироваться на мероприятие %s," +
                    " если оно уже начато или законченно")
                    .formatted(eventEntity.getName()));
        }
        boolean result = registrationRepository.alreadyRegister(eventEntity, userIdRegistration);
        int currentCount = registrationRepository.countByEvent(eventEntity);
        if (result) {
            throw new AlreadyRegisterException("Пользователь %s с id=%d уже зарегистрирован на мероприятие /'%s/'"
                    .formatted(userIdRegistration.getLogin(), userIdRegistration.getId(), eventEntity.getName()));
        }
        if (currentCount >= eventEntity.getMaxPlaces()) {
            throw new EventFullException("Мероприятие предусмотрено на %d мест, занято %d, регистрация невозможна"
                    .formatted(eventEntity.getMaxPlaces(), eventEntity.getRegistrations().size()));
        }
    }

    @Transactional
    public void cancelEvent(Long id) {
        log.info("Отмена регистрации на мероприятие ID: {}", id);
        UserEntity userIdRegistration = eventUserCheckService.checkToFindUser();
        EventEntity eventEntity = checkFindEventByIdHelper(id);
        checkOnCancel(eventEntity, userIdRegistration);
        log.info("Отменена регистрация пользователя {}, мероприятия {}", userIdRegistration.getLogin(), eventEntity.getName());
    }

    private void checkOnCancel(EventEntity eventEntity, UserEntity userIdRegistration) {
        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            throw new EventStatusException(("Нельзя отменить регистрацию на мероприятие %s," +
                    " если оно уже начато или законченно")
                    .formatted(eventEntity.getName()));
        }
        Optional<RegistrationEntity> userRegistration = registrationRepository.findByEventAndUserId(eventEntity,
                userIdRegistration.getId());
        if (userRegistration.isPresent()) {
            registrationRepository.delete(userRegistration.get());
            return;
        }
        throw new RegistrationNotFoundException("У пользователя %s с id=%d нет такой регистрации"
                .formatted(userIdRegistration.getLogin(), userIdRegistration.getId()));
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

    public List<EventResponseDto> getEventsOfTheCurrentUser() {
        UserEntity userIdRegistration = eventUserCheckService.checkToFindUser();
        log.debug("Поиск мероприятий для пользователя: {}", userIdRegistration.getLogin());
        List<EventEntity> getEventForUser = eventRepository.getEventForCurrentUser(userIdRegistration.getId());
        return checkOnFindEventForUser(getEventForUser, userIdRegistration);
    }

    private List<EventResponseDto> checkOnFindEventForUser(List<EventEntity> getEventForUser, UserEntity userIdRegistration) {
        if (getEventForUser.isEmpty()) {
            throw new RegistrationNotFoundException("Пользователь не записан на какое-либо мероприятие");
        }
        return getEventForUser.stream().map(eventConverter::convertEvenEntityToEvenDto).toList();
    }

    private void KafkaSendMessage(EventEntity old, EventEntity newEntity, UserEntity currentUser) {
        EventChangeMessage newMessage = new EventChangeMessage(
                UUID.randomUUID(),
                "UPDATE",
                newEntity.getId(),
                LocalDateTime.now(),
                newEntity.getOwner().getId(),
                currentUser.getId(),
                registrationRepository.findUserIdsByEventId(newEntity.getId()),
                eventComparator.compareToEvent(old, newEntity)
        );
        eventChangeProducer.send(newMessage);
    }
}

