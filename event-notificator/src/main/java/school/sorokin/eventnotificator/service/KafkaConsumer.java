package school.sorokin.eventnotificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import school.sorokin.eventcommon.kafka.EventChangeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final NotificationProcessor notificationProcessor;

    @KafkaListener(topics = "event-change-topic", groupId = "event-notificator-group")
    public void consume (EventChangeMessage message){
        notificationProcessor.processor(message);
    }

}
