package school.sorokin.eventnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sorokin.eventnotificator.entity.NotificationEventPayloadEntity;

@Repository
public interface NotificationEventPayloadRepository extends JpaRepository<NotificationEventPayloadEntity, Long> {


    NotificationEventPayloadEntity findByPayloadId(Long payloadId);
}
