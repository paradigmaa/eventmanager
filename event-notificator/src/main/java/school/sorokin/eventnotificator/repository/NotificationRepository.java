package school.sorokin.eventnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.sorokin.eventnotificator.entity.NotificationEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT n from NotificationEntity n WHERE n.userId = :id AND  n.isRead = false")
    List<NotificationEntity> findByUserIdAndIsReadFalse(@Param("id") Long id);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.notificationId IN :ids and " +
            "n.userId = :userId and n.isRead = false")
    int markNotification(@Param("ids") List<Long> ids, @Param("userId") Long userId);

    @Query("SELECT n.payLoadId FROM NotificationEntity n WHERE n.createdAt < :createdAt")
    List<Long>listForDelete(@Param("createdAt")LocalDateTime created);

    @Modifying
    @Query("DELETE from NotificationEntity WHERE payLoadId = :payloadId")
    int deleteOldNotifications(@Param("payloadId") Long payload);

    @Query("SELECT n.notificationId from NotificationEntity n WHERE n.userId = :id AND  n.isRead = false")
    List<Long> findByUserIdAndIsReadFalseNotificationId(@Param("id") Long id);


}
