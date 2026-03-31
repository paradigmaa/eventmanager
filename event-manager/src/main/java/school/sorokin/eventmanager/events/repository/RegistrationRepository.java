package school.sorokin.eventmanager.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.RegistrationEntity;
import school.sorokin.eventmanager.users.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {


    @Query("SELECT COUNT(r) > 0 FROM RegistrationEntity r " +
            "WHERE r.event = :event AND r.user = :user")
    boolean alreadyRegister(@Param("event") EventEntity event,
                            @Param("user") UserEntity user);

    @Query("SELECT COUNT(r) FROM RegistrationEntity r WHERE r.event = :event")
    int countByEvent(@Param("event") EventEntity event);

    Optional<RegistrationEntity> findByEventAndUserId(EventEntity eventEntity, Long id);


    @Query("SELECT r.user.id FROM RegistrationEntity  r WHERE r.event.id = :eventId")
    List<Long> findUserIdsByEventId(@Param("eventId") Long eventId);
}
