package school.sorokin.eventnotificator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<NotificationResponseDto>> getNotReadNotificationForUser() throws JsonProcessingException {
        return ResponseEntity.ok(notificationService.getNotReadNotificationForUser());
    }

    @PostMapping("/notifications")
    public ResponseEntity<Void> readAllNotifications() throws JsonProcessingException {
        notificationService.readAllNotification();
        return ResponseEntity.ok().build();
    }
}
