package school.sorokin.eventmanager.events.exception;

public class EventFullException extends RuntimeException {
    public EventFullException(String message) {
        super(message);
    }
}
