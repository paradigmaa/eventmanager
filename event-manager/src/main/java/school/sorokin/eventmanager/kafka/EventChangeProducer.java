package school.sorokin.eventmanager.kafka;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.sorokin.eventcommon.kafka.EventChangeMessage;


@Component
@RequiredArgsConstructor
public class EventChangeProducer{

    private final KafkaTemplate<String, EventChangeMessage> kafkaTemplate;
    private static final String TOPIC = "event-change-topic";


    public void send(EventChangeMessage message) {
        kafkaTemplate.send(TOPIC, message);
    }
}



