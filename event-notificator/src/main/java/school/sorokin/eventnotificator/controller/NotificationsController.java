package school.sorokin.eventnotificator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sorokin.eventnotificator.dto.NotificationResponseDto;
import school.sorokin.eventnotificator.service.KafkaConsumer;
import school.sorokin.eventnotificator.service.NotificationService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NotificationsController {

    private final KafkaConsumer kafkaConsumer;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<List<NotificationResponseDto>> getNotReadNotificationForUser() throws JsonProcessingException {
        log.info("Запрос на получение нотификаций пользователя");
        return ResponseEntity.ok(notificationService.getNotReadNotificationForUser());
    }

    @PostMapping("/notifications")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<Void> readAllNotifications() throws JsonProcessingException {
        log.info("Отметить все нотификации прочитанными");
        notificationService.readAllNotification();
        log.info("Все нотификации прочтены");
        return ResponseEntity.ok().build();
    }
}
