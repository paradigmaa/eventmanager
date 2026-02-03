package school.sorokin.eventmanager.events.exception;

public class AlreadyRegisterException extends RuntimeException {
    public AlreadyRegisterException(String message) {
        super(message);
    }
}
