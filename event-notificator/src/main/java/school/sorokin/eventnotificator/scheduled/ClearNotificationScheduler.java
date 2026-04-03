package school.sorokin.eventnotificator.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventnotificator.entity.NotificationEntity;
import school.sorokin.eventnotificator.repository.NotificationEventPayloadRepository;
import school.sorokin.eventnotificator.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClearNotificationScheduler {
    private final NotificationRepository notificationRepository;
    private final NotificationEventPayloadRepository notificationEventPayloadRepository;

    @Scheduled(cron = "${scheduler.cron}")
    @Transactional
    public void clearNotification() {
        log.info("=== ШЕДУЛЕР ЗАПУЩЕН ===");
        LocalDateTime sevenDay = LocalDateTime.now().minusDays(7);
        List<Long> notificationListForDelete = notificationRepository.listForDelete(sevenDay);
        if(!notificationListForDelete.isEmpty()) {
            notificationListForDelete.forEach(n -> {
                int deletedNotifications = notificationRepository.deleteOldNotifications(n);
                log.info("Было удалено нотификаций пользователя: {}", deletedNotifications);
                int deletePayLoad = notificationEventPayloadRepository.deleteByPayLoadMessage(n);
                log.info("Было удалено нотификаций PayLoad: {}", deletePayLoad);
            });
        }
        log.info("=== ШЕДУЛЕР ЗАВЕРШИЛ ===");
    }
}