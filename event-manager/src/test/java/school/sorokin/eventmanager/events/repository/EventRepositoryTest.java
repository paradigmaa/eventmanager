package school.sorokin.eventmanager.events.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.EventStatus;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.locations.repository.LocationRepository;
import school.sorokin.eventmanager.users.dto.RoleUsers;
import school.sorokin.eventmanager.users.entity.UserEntity;
import school.sorokin.eventmanager.users.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;

    private UserEntity createAndSaveUser() {
        UserEntity user = new UserEntity();
        user.setLogin("testuser_" + System.currentTimeMillis());
        user.setPasswordHash("$2a$10$encryptedPassword");
        user.setAge(25);
        user.setRole(RoleUsers.USER);
        return userRepository.save(user);
    }

    private LocationEntity createAndSaveLocation() {
        LocationEntity location = new LocationEntity();
        location.setName("Test Location");
        location.setAddress("Test Address, 123");
        location.setCapacity(100);
        location.setDescription("This is a test location for events");
        return locationRepository.save(location);
    }

    private EventEntity createTestEvent(String name,
                                        BigDecimal cost,
                                        Integer maxPlaces,
                                        UserEntity owner,
                                        LocationEntity location) {
        EventEntity event = new EventEntity();
        event.setName(name);
        event.setDateTime(LocalDateTime.now().plusDays(1));
        event.setCost(cost);
        event.setDuration(60);
        event.setMaxPlaces(maxPlaces);
        event.setOwner(owner);
        event.setLocation(location);
        event.setStatus(EventStatus.WAIT_START);
        return event;
    }

    @Test
    void saveEvent_ShouldGenerateId_WhenEventIsSaved() {
        UserEntity owner = createAndSaveUser();
        LocationEntity location = createAndSaveLocation();
        EventEntity event = createTestEvent("Test Event", BigDecimal.valueOf(100), 50, owner, location);

        EventEntity savedEvent = eventRepository.save(event);

        assertThat(savedEvent.getId()).isNotNull();
        assertThat(savedEvent.getName()).isEqualTo("Test Event");
        assertThat(savedEvent.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(savedEvent.getLocation().getId()).isEqualTo(location.getId());
        assertThat(savedEvent.getStatus()).isEqualTo(EventStatus.WAIT_START);
    }

    @Test
    void findEvents_ShouldFindByExactName() {
        UserEntity owner = createAndSaveUser();
        LocationEntity location = createAndSaveLocation();

        EventEntity event1 = createTestEvent("Java Conference", BigDecimal.valueOf(100), 50, owner, location);
        EventEntity event2 = createTestEvent("Python Workshop", BigDecimal.valueOf(50), 30, owner, location);

        eventRepository.saveAll(List.of(event1, event2));

        Pageable pageable = PageRequest.of(0, 10);

        Page<EventEntity> result = eventRepository.findEvents(
                "Java Conference",
                null,
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Java Conference");
    }

    @Test
    void findEvents_ShouldFindByNameWithLike() {
        UserEntity owner = createAndSaveUser();
        LocationEntity location = createAndSaveLocation();

        EventEntity event1 = createTestEvent("Java Conference 2024", BigDecimal.valueOf(100), 50, owner, location);
        EventEntity event2 = createTestEvent("Java Meetup", BigDecimal.valueOf(50), 30, owner, location);
        EventEntity event3 = createTestEvent("Python Workshop", BigDecimal.valueOf(75), 40, owner, location);

        eventRepository.saveAll(List.of(event1, event2, event3));

        Pageable pageable = PageRequest.of(0, 10);

        Page<EventEntity> result = eventRepository.findEvents(
                "Java",
                null,
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(EventEntity::getName)
                .containsExactlyInAnyOrder("Java Conference 2024", "Java Meetup");
    }

    @Test
    void findEvents_ShouldFilterByCostRange() {
        UserEntity owner = createAndSaveUser();
        LocationEntity location = createAndSaveLocation();

        EventEntity cheap = createTestEvent("Cheap Event", BigDecimal.valueOf(50), 30, owner, location);
        EventEntity medium = createTestEvent("Medium Event", BigDecimal.valueOf(100), 40, owner, location);
        EventEntity expensive = createTestEvent("Expensive Event", BigDecimal.valueOf(200), 50, owner, location);

        eventRepository.saveAll(List.of(cheap, medium, expensive));

        Pageable pageable = PageRequest.of(0, 10);

        Page<EventEntity> result = eventRepository.findEvents(
                null,
                null,
                null,
                BigDecimal.valueOf(80),
                BigDecimal.valueOf(150),
                null,
                null,
                pageable
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Medium Event");
    }

    @Test
    void findEvents_ShouldFilterByPlacesRange() {
        UserEntity owner = createAndSaveUser();
        LocationEntity location = createAndSaveLocation();

        EventEntity small = createTestEvent("Small Event", BigDecimal.valueOf(100), 20, owner, location);
        EventEntity medium = createTestEvent("Medium Event", BigDecimal.valueOf(100), 50, owner, location);
        EventEntity large = createTestEvent("Large Event", BigDecimal.valueOf(100), 100, owner, location);

        eventRepository.saveAll(List.of(small, medium, large));

        Pageable pageable = PageRequest.of(0, 10);

        Page<EventEntity> result = eventRepository.findEvents(
                null,
                30,
                80,
                null,
                null,
                null,
                null,
                pageable
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Medium Event");
    }

    @Test
    void findEvents_ShouldReturnAll_WhenNoFiltersProvided() {
        UserEntity owner = createAndSaveUser();
        LocationEntity location = createAndSaveLocation();

        EventEntity event1 = createTestEvent("Event 1", BigDecimal.valueOf(100), 30, owner, location);
        EventEntity event2 = createTestEvent("Event 2", BigDecimal.valueOf(150), 40, owner, location);

        eventRepository.saveAll(List.of(event1, event2));

        Pageable pageable = PageRequest.of(0, 10);

        Page<EventEntity> result = eventRepository.findEvents(
                null, null, null, null, null, null, null, pageable
        );

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void existsByName_ShouldReturnTrue_WhenEventExists() {
        UserEntity owner = createAndSaveUser();
        LocationEntity location = createAndSaveLocation();
        EventEntity event = createTestEvent("Unique Event Name", BigDecimal.valueOf(100), 50, owner, location);
        eventRepository.save(event);

        boolean exists = eventRepository.existsByName("Unique Event Name");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_ShouldReturnFalse_WhenEventDoesNotExist() {
        boolean exists = eventRepository.existsByName("Non Existent Event Name");

        assertThat(exists).isFalse();
    }

    @Test
    void findByOwnerIdWithDetails_ShouldReturnEventsWithDetails() {
        UserEntity owner = createAndSaveUser();
        LocationEntity location = createAndSaveLocation();

        EventEntity event1 = createTestEvent("Owner Event 1", BigDecimal.valueOf(100), 50, owner, location);
        EventEntity event2 = createTestEvent("Owner Event 2", BigDecimal.valueOf(150), 30, owner, location);

        eventRepository.saveAll(List.of(event1, event2));

        List<EventEntity> events = eventRepository.findByOwnerIdWithDetails(owner.getId());

        assertThat(events).hasSize(2);
        assertThat(events)
                .extracting(EventEntity::getName)
                .containsExactlyInAnyOrder("Owner Event 1", "Owner Event 2");
    }
}