package school.sorokin.eventnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.sorokin.eventnotificator.entity.NotificationEventPayloadEntity;

@Repository
public interface NotificationEventPayloadRepository extends JpaRepository<NotificationEventPayloadEntity, Long> {


    @Query("SELECT p FROM NotificationEventPayloadEntity p WHERE p.payloadId = :payloadId")
    NotificationEventPayloadEntity findByPayloadId(@Param("payloadId") Long payloadId);


    @Modifying
    @Query("DELETE from NotificationEventPayloadEntity WHERE payloadId = :payloadId")
    int deleteByPayLoadMessage(@Param("payloadId") Long payload);
}
