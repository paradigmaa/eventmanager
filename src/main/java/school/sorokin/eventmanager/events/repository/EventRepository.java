package school.sorokin.eventmanager.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("SELECT e FROM EventEntity e " +
            "WHERE (:name IS NULL OR e.name LIKE %:name%) " +
            "AND (:placesMin IS NULL OR e.maxPlaces >= :placesMin) " +
            "AND (:placesMax IS NULL OR e.maxPlaces <= :placesMax) " +
            "AND (:costMin IS NULL OR e.cost >= :costMin) " +
            "AND (:costMax IS NULL OR e.cost <= :costMax) " +
            "AND (:locationId IS NULL OR e.location.id = :locationId) " +
            "AND (:eventStatus IS NULL OR e.status = :eventStatus)")
    Page<EventEntity> searchEvents(
            @Param("name") String name,
            @Param("placesMin") Integer placesMin,
            @Param("placesMax") Integer placesMax,
            @Param("costMin") BigDecimal costMin,
            @Param("costMax") BigDecimal costMax,
            @Param("locationId") Long locationId,
            @Param("eventStatus") EventStatus eventStatus,
            Pageable pageable
    );

    @Query("SELECT DISTINCT e from EventEntity e "
            + "LEFT JOIN FETCH e.registrations "
            + "LEFT JOIN FETCH e.location "
            + "WHERE e.owner.id = :ownerId")
    List<EventEntity> findByOwnerIdWithDetails(Long ownerId);


    @Query("SELECT DISTINCT e from EventEntity e " +
            "LEFT JOIN FETCH e.registrations " +
            "WHERE e.id = :eventId")
    Optional<EventEntity> findEventForRegistration(@Param("eventId") Long eventId);


    @Query("SELECT e, SIZE(e.registrations) from EventEntity e " +
            "LEFT JOIN FETCH e.owner " +
            "LEFT JOIN FETCH e.location " +
            "JOIN e.registrations r " +
            "WHERE r.user.id = :userId")
    List<Object[]> getAllEventsCurrentUSer(@Param("userId") Long userId);

    @Query("SELECT  e from EventEntity e " +
            "WHERE e.dateTime <= :currentTime " +
            "AND e.status = :status")
    List<EventEntity> findByDateTimeBeforeAndStatus(@Param("currentTime") LocalDateTime dateTimeBefore,
                                                    @Param("status") EventStatus status);

    List<EventEntity> findByStatus(EventStatus eventStatus);
}
