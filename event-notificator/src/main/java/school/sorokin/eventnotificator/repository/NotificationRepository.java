package school.sorokin.eventnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.sorokin.eventnotificator.entity.NotificationEntity;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT n from NotificationEntity n WHERE n.userId = :id AND  n.isRead = false")
    List<NotificationEntity> findByUserIdAndIsReadFalse(@Param("id") Long id);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = CURRENT TIMESTAMP WHERE n.userId = :userId and n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);
}
