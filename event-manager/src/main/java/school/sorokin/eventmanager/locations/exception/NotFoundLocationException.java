package school.sorokin.eventmanager.locations.exception;

public class NotFoundLocationException extends RuntimeException {
    public NotFoundLocationException(String message) {
        super(message);
    }
}
