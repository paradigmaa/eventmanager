package school.sorokin.eventmanager.events.exception;

public class EventNotFoundException extends RuntimeException {
    private final Long id;

    public EventNotFoundException(Long id) {
        super(createMessage(id));
        this.id = id;
    }

    private static String createMessage(Long id) {
        return String.format(
                "Мероприятия с id=%d не существует".formatted(id)
        );
    }
}
