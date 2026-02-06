package school.sorokin.eventmanager.events.exception;

public class AlreadyEventNameExistException extends RuntimeException {
    public AlreadyEventNameExistException(String message) {
        super(message);
    }
}
