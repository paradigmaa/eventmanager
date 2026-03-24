package school.sorokin.eventmanager.kafka;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.sorokin.eventcommon.kafka.EventChangeMessage;


@Component
public class EventChangeProducer{
    private final KafkaTemplate<String, EventChangeMessage> kafkaTemplate;
    private static final String TOPIC = "event-change-topic";

    public EventChangeProducer(KafkaTemplate<String, EventChangeMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(EventChangeMessage message) {
        kafkaTemplate.send(TOPIC, message);
    }
}



