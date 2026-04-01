package school.sorokin.eventmanager.events.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.sorokin.eventmanager.events.EventConverter;
import school.sorokin.eventmanager.events.RegistrationConverter;
import school.sorokin.eventmanager.events.domain.Event;
import school.sorokin.eventmanager.events.dto.*;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.EventStatus;
import school.sorokin.eventmanager.events.entity.RegistrationEntity;
import school.sorokin.eventmanager.events.exception.*;
import school.sorokin.eventmanager.events.repository.EventRepository;
import school.sorokin.eventmanager.events.repository.RegistrationRepository;
import school.sorokin.eventmanager.kafka.EventChangeProducer;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.dto.RoleUsers;
import school.sorokin.eventmanager.users.entity.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventConverter eventConverter;

    @Mock
    private RegistrationConverter registrationConverter;

    @Mock
    private EventUserCheckService eventUserCheckService;

    @Mock
    private EventLocationCheckService eventLocationCheckService;

    @InjectMocks
    private EventService eventService;

    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setLogin("testuser");
        user.setPasswordHash("password");
        user.setAge(25);
        user.setRole(RoleUsers.USER);
        return user;
    }

    private UserEntity createTestAdmin() {
        UserEntity admin = new UserEntity();
        admin.setId(2L);
        admin.setLogin("admin");
        admin.setPasswordHash("password");
        admin.setAge(30);
        admin.setRole(RoleUsers.ADMIN);
        return admin;
    }

    private LocationEntity createTestLocation() {
        LocationEntity location = new LocationEntity();
        location.setId(1L);
        location.setName("Test Location");
        location.setAddress("Test Address");
        location.setCapacity(100);
        location.setDescription("Test Description");
        return location;
    }

    private EventEntity createTestEvent() {
        EventEntity event = new EventEntity();
        event.setId(1L);
        event.setName("Test Event");
        event.setDateTime(LocalDateTime.now().plusDays(1));
        event.setCost(BigDecimal.valueOf(100));
        event.setDuration(60);
        event.setMaxPlaces(50);
        event.setOwner(createTestUser());
        event.setLocation(createTestLocation());
        event.setStatus(EventStatus.WAIT_START);
        return event;
    }

    private EventEntity createTestEventWithOwner(UserEntity owner) {
        EventEntity event = createTestEvent();
        event.setOwner(owner);
        return event;
    }

    private EventEntity createTestEventWithStatus(EventStatus status) {
        EventEntity event = createTestEvent();
        event.setStatus(status);
        return event;
    }

    private EventResponseDto createTestEventResponseDto() {
        return new EventResponseDto(
                1L,
                "Test Event",
                1L,
                50,
                0,
                LocalDateTime.now().plusDays(1),
                BigDecimal.valueOf(100),
                60,
                1L,
                EventStatus.WAIT_START
        );
    }

    private EventCreateRequestDto createTestEventCreateRequestDto() {
        return new EventCreateRequestDto(
                "New Event",
                50,
                LocalDateTime.now().plusDays(1),
                BigDecimal.valueOf(100),
                60,
                1L
        );
    }

    private RegistrationEntity createTestRegistration() {
        RegistrationEntity registration = new RegistrationEntity();
        registration.setId(1L);
        registration.setEvent(createTestEvent());
        registration.setUser(createTestUser());
        return registration;
    }

    private RegistrationResponseDto createTestRegistrationResponseDto() {
        return new RegistrationResponseDto(1L, "Test Event", "testuser");
    }

    @Test
    void createEvent_ShouldSaveEvent_WhenValidData() {
        EventCreateRequestDto request = createTestEventCreateRequestDto();
        LocationEntity location = createTestLocation();
        UserEntity owner = createTestUser();
        Event domainEvent = mock(Event.class);
        EventEntity eventEntity = createTestEvent();
        EventResponseDto responseDto = createTestEventResponseDto();

        when(eventLocationCheckService.checkLocationId(1L)).thenReturn(location);
        when(eventUserCheckService.checkToFindUser()).thenReturn(owner);
        when(eventConverter.convertEventCreateRequestDtoToEvent(request, location, owner)).thenReturn(domainEvent);
        when(domainEvent.name()).thenReturn("New Event");
        when(domainEvent.maxPlaces()).thenReturn(50);
        when(domainEvent.dateTime()).thenReturn(LocalDateTime.now().plusDays(1));
        when(eventRepository.existsByName("New Event")).thenReturn(false);
        when(domainEvent.dateTime()).thenReturn(LocalDateTime.now().plusDays(1));
        when(eventConverter.convertEventToEventEntity(domainEvent)).thenReturn(eventEntity);
        when(eventRepository.save(eventEntity)).thenReturn(eventEntity);
        when(eventConverter.convertEvenEntityToEvenDto(eventEntity)).thenReturn(responseDto);

        EventResponseDto result = eventService.createEvent(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(eventRepository, times(1)).save(eventEntity);
    }

    @Test
    void createEvent_ShouldThrowException_WhenNameAlreadyExists() {
        EventCreateRequestDto request = createTestEventCreateRequestDto();
        LocationEntity location = createTestLocation();
        UserEntity owner = createTestUser();
        Event domainEvent = mock(Event.class);

        when(eventLocationCheckService.checkLocationId(1L)).thenReturn(location);
        when(eventUserCheckService.checkToFindUser()).thenReturn(owner);
        when(eventConverter.convertEventCreateRequestDtoToEvent(request, location, owner)).thenReturn(domainEvent);
        when(domainEvent.name()).thenReturn("New Event");
        when(eventRepository.existsByName("New Event")).thenReturn(true);

        assertThatThrownBy(() -> eventService.createEvent(request))
                .isInstanceOf(AlreadyEventNameExistException.class)
                .hasMessageContaining("Мероприятие с таким именем уже существует");

        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_ShouldThrowException_WhenDateIsInPast() {
        EventCreateRequestDto request = new EventCreateRequestDto(
                "New Event",
                50,
                LocalDateTime.now().minusDays(1),
                BigDecimal.valueOf(100),
                60,
                1L
        );
        LocationEntity location = createTestLocation();
        UserEntity owner = createTestUser();
        Event domainEvent = mock(Event.class);

        when(eventLocationCheckService.checkLocationId(1L)).thenReturn(location);
        when(eventUserCheckService.checkToFindUser()).thenReturn(owner);
        when(eventConverter.convertEventCreateRequestDtoToEvent(request, location, owner)).thenReturn(domainEvent);
        when(domainEvent.name()).thenReturn("New Event");
        when(eventRepository.existsByName("New Event")).thenReturn(false);
        when(domainEvent.dateTime()).thenReturn(LocalDateTime.now().minusDays(1));

        assertThatThrownBy(() -> eventService.createEvent(request))
                .isInstanceOf(EventDateException.class)
                .hasMessageContaining("Дата мероприятия не может быть в прошлом");

        verify(eventRepository, never()).save(any());
    }

    @Test
    void deleteEvent_ShouldCancelEvent_WhenUserIsOwner() {
        Long eventId = 1L;
        UserEntity owner = createTestUser();
        EventEntity event = createTestEventWithOwner(owner);

        when(eventUserCheckService.checkToFindUser()).thenReturn(owner);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);

        String result = eventService.deleteEvent(eventId);

        assertThat(result).isEqualTo("Мероприятие удалено");
        assertThat(event.getStatus()).isEqualTo(EventStatus.CANCELED);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void deleteEvent_ShouldCancelEvent_WhenUserIsAdmin() {
        Long eventId = 1L;
        UserEntity admin = createTestAdmin();
        UserEntity owner = createTestUser();
        EventEntity event = createTestEventWithOwner(owner);

        when(eventUserCheckService.checkToFindUser()).thenReturn(admin);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);

        String result = eventService.deleteEvent(eventId);

        assertThat(result).isEqualTo("Мероприятие удалено");
        assertThat(event.getStatus()).isEqualTo(EventStatus.CANCELED);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void deleteEvent_ShouldThrowException_WhenUserIsNotOwnerAndNotAdmin() {
        Long eventId = 1L;
        UserEntity owner = createTestUser();
        UserEntity otherUser = new UserEntity();
        otherUser.setId(3L);
        otherUser.setLogin("other");
        otherUser.setRole(RoleUsers.USER);
        EventEntity event = createTestEventWithOwner(owner);

        when(eventUserCheckService.checkToFindUser()).thenReturn(otherUser);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.deleteEvent(eventId))
                .isInstanceOf(EventDeleteException.class)
                .hasMessageContaining("Мероприятие может удалить только владелец или администратор");

        verify(eventRepository, never()).save(any());
    }

    @Test
    void deleteEvent_ShouldThrowException_WhenEventAlreadyStarted() {
        Long eventId = 1L;
        UserEntity owner = createTestUser();
        EventEntity event = createTestEventWithStatus(EventStatus.STARTED);
        event.setOwner(owner);

        when(eventUserCheckService.checkToFindUser()).thenReturn(owner);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.deleteEvent(eventId))
                .isInstanceOf(EventStatusException.class)
                .hasMessageContaining("Мероприятие уже началось, удаление невозможно");

        verify(eventRepository, never()).save(any());
    }

    @Test
    void findEventById_ShouldReturnEvent_WhenExists() {
        Long eventId = 1L;
        EventEntity event = createTestEvent();
        EventResponseDto responseDto = createTestEventResponseDto();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventConverter.convertEvenEntityToEvenDto(event)).thenReturn(responseDto);

        EventResponseDto result = eventService.findEventById(eventId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findEventById_ShouldThrowException_WhenNotFound() {
        Long eventId = 999L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.findEventById(eventId))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Мероприятие не найдено");
    }

    @Test
    void getCreatedEventsOfTheCurrentUser_ShouldReturnEvents_WhenExists() {
        UserEntity owner = createTestUser();
        EventEntity event1 = createTestEvent();
        EventEntity event2 = createTestEvent();
        event2.setId(2L);
        event2.setName("Test Event 2");
        List<EventEntity> events = List.of(event1, event2);
        EventResponseDto responseDto1 = createTestEventResponseDto();
        EventResponseDto responseDto2 = new EventResponseDto(
                2L, "Test Event 2", 1L, 50, 0,
                LocalDateTime.now().plusDays(1), BigDecimal.valueOf(100), 60, 1L, EventStatus.WAIT_START
        );

        when(eventUserCheckService.checkToFindUser()).thenReturn(owner);
        when(eventRepository.findByOwnerIdWithDetails(owner.getId())).thenReturn(events);
        when(eventConverter.convertEvenEntityToEvenDto(event1)).thenReturn(responseDto1);
        when(eventConverter.convertEvenEntityToEvenDto(event2)).thenReturn(responseDto2);

        List<EventResponseDto> result = eventService.getCreatedEventsOfTheCurrentUser();

        assertThat(result).hasSize(2);
        verify(eventRepository, times(1)).findByOwnerIdWithDetails(owner.getId());
    }

    @Test
    void getCreatedEventsOfTheCurrentUser_ShouldReturnEmptyList_WhenNoEvents() {
        UserEntity owner = createTestUser();

        when(eventUserCheckService.checkToFindUser()).thenReturn(owner);
        when(eventRepository.findByOwnerIdWithDetails(owner.getId())).thenReturn(List.of());

        List<EventResponseDto> result = eventService.getCreatedEventsOfTheCurrentUser();

        assertThat(result).isEmpty();
    }

    @Test
    void getEventsByFilter_ShouldReturnPageOfEvents() {
        EventPagination pagination = new EventPagination(
                "Java",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                10
        );
        Pageable pageable = PageRequest.of(0, 10);
        EventEntity event = createTestEvent();
        event.setName("Java Event");
        List<EventEntity> events = List.of(event);
        Page<EventEntity> page = new PageImpl<>(events, pageable, 1);
        EventResponseDto responseDto = new EventResponseDto(
                1L, "Java Event", 1L, 50, 0,
                LocalDateTime.now().plusDays(1), BigDecimal.valueOf(100), 60, 1L, EventStatus.WAIT_START
        );

        when(eventRepository.findEvents(
                pagination.name(),
                pagination.placesMin(),
                pagination.placesMax(),
                pagination.costMin(),
                pagination.costMax(),
                pagination.locationId(),
                pagination.eventStatus(),
                pageable
        )).thenReturn(page);
        when(eventConverter.convertEvenEntityToEvenDto(event)).thenReturn(responseDto);

        Page<EventResponseDto> result = eventService.getEventsByFilter(pagination);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Java Event");
    }

    @Test
    void registrationUserForTheEvent_ShouldRegister_WhenValid() {
        Long eventId = 1L;
        UserEntity user = createTestUser();
        EventEntity event = createTestEvent();
        RegistrationEntity registration = createTestRegistration();
        RegistrationResponseDto responseDto = createTestRegistrationResponseDto();

        when(eventUserCheckService.checkToFindUser()).thenReturn(user);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(registrationRepository.alreadyRegister(event, user)).thenReturn(false);
        when(registrationRepository.countByEvent(event)).thenReturn(10);
        when(registrationRepository.save(any(RegistrationEntity.class))).thenReturn(registration);
        when(registrationConverter.registrationToDto(registration)).thenReturn(responseDto);

        RegistrationResponseDto result = eventService.registrationUserForTheEvent(eventId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.eventName()).isEqualTo("Test Event");
        assertThat(result.userName()).isEqualTo("testuser");
        verify(registrationRepository, times(1)).save(any(RegistrationEntity.class));
        verify(registrationConverter, times(1)).registrationToDto(registration);
    }

    @Test
    void registrationUserForTheEvent_ShouldThrowException_WhenEventNotWaitStart() {
        Long eventId = 1L;
        UserEntity user = createTestUser();
        EventEntity event = createTestEventWithStatus(EventStatus.STARTED);

        when(eventUserCheckService.checkToFindUser()).thenReturn(user);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.registrationUserForTheEvent(eventId))
                .isInstanceOf(EventStatusException.class)
                .hasMessageContaining("Нельзя зарегистрироваться");

        verify(registrationRepository, never()).save(any());
    }

    @Test
    void registrationUserForTheEvent_ShouldThrowException_WhenAlreadyRegistered() {
        Long eventId = 1L;
        UserEntity user = createTestUser();
        EventEntity event = createTestEvent();

        when(eventUserCheckService.checkToFindUser()).thenReturn(user);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(registrationRepository.alreadyRegister(event, user)).thenReturn(true);

        assertThatThrownBy(() -> eventService.registrationUserForTheEvent(eventId))
                .isInstanceOf(AlreadyRegisterException.class)
                .hasMessageContaining("уже зарегистрирован");

        verify(registrationRepository, never()).save(any());
    }

    @Test
    void registrationUserForTheEvent_ShouldThrowException_WhenEventFull() {
        Long eventId = 1L;
        UserEntity user = createTestUser();
        EventEntity event = createTestEvent();

        when(eventUserCheckService.checkToFindUser()).thenReturn(user);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(registrationRepository.alreadyRegister(event, user)).thenReturn(false);
        when(registrationRepository.countByEvent(event)).thenReturn(50);

        assertThatThrownBy(() -> eventService.registrationUserForTheEvent(eventId))
                .isInstanceOf(EventFullException.class)
                .hasMessageContaining("регистрация невозможна");

        verify(registrationRepository, never()).save(any());
    }

    @Test
    void cancelEvent_ShouldCancelRegistration_WhenValid() {
        Long eventId = 1L;
        UserEntity user = createTestUser();
        EventEntity event = createTestEvent();
        RegistrationEntity registration = createTestRegistration();

        when(eventUserCheckService.checkToFindUser()).thenReturn(user);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(registrationRepository.findByEventAndUserId(event, user.getId())).thenReturn(Optional.of(registration));

        eventService.cancelEvent(eventId);

        verify(registrationRepository, times(1)).delete(registration);
    }

    @Test
    void cancelEvent_ShouldThrowException_WhenEventNotWaitStart() {
        Long eventId = 1L;
        UserEntity user = createTestUser();
        EventEntity event = createTestEventWithStatus(EventStatus.STARTED);

        when(eventUserCheckService.checkToFindUser()).thenReturn(user);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.cancelEvent(eventId))
                .isInstanceOf(EventStatusException.class)
                .hasMessageContaining("Нельзя отменить регистрацию");

        verify(registrationRepository, never()).delete(any());
    }

    @Test
    void cancelEvent_ShouldThrowException_WhenRegistrationNotFound() {
        Long eventId = 1L;
        UserEntity user = createTestUser();
        EventEntity event = createTestEvent();

        when(eventUserCheckService.checkToFindUser()).thenReturn(user);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(registrationRepository.findByEventAndUserId(event, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.cancelEvent(eventId))
                .isInstanceOf(RegistrationNotFoundException.class)
                .hasMessageContaining("нет такой регистрации");

        verify(registrationRepository, never()).delete(any());
    }

    @Test
    void getEventsOfTheCurrentUser_ShouldReturnEvents_WhenUserHasRegistrations() {
        UserEntity user = createTestUser();
        EventEntity event1 = createTestEvent();
        EventEntity event2 = createTestEvent();
        event2.setId(2L);
        event2.setName("Test Event 2");
        List<EventEntity> events = List.of(event1, event2);
        EventResponseDto responseDto1 = createTestEventResponseDto();
        EventResponseDto responseDto2 = new EventResponseDto(
                2L, "Test Event 2", 1L, 50, 0,
                LocalDateTime.now().plusDays(1), BigDecimal.valueOf(100), 60, 1L, EventStatus.WAIT_START
        );

        when(eventUserCheckService.checkToFindUser()).thenReturn(user);
        when(eventRepository.getEventForCurrentUser(user.getId())).thenReturn(events);
        when(eventConverter.convertEvenEntityToEvenDto(event1)).thenReturn(responseDto1);
        when(eventConverter.convertEvenEntityToEvenDto(event2)).thenReturn(responseDto2);

        List<EventResponseDto> result = eventService.getEventsOfTheCurrentUser();

        assertThat(result).hasSize(2);
    }

    @Test
    void getEventsOfTheCurrentUser_ShouldThrowException_WhenNoRegistrations() {
        UserEntity user = createTestUser();

        when(eventUserCheckService.checkToFindUser()).thenReturn(user);
        when(eventRepository.getEventForCurrentUser(user.getId())).thenReturn(List.of());

        assertThatThrownBy(() -> eventService.getEventsOfTheCurrentUser())
                .isInstanceOf(RegistrationNotFoundException.class)
                .hasMessageContaining("Пользователь не записан на какое-либо мероприятие");
    }
}
