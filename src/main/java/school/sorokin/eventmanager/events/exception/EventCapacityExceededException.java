package school.sorokin.eventmanager.events.exception;

public class EventCapacityExceededException extends RuntimeException {

    private final String locationName;
    private final int capacity;
    private final int maxPlaces;
    private final int deficit;


    public EventCapacityExceededException(String locationName, int capacity, int maxPlaces, int deficit) {
        super(createMessage(locationName, capacity, maxPlaces, deficit));
        this.locationName = locationName;
        this.capacity = capacity;
        this.maxPlaces = maxPlaces;
        this.deficit = deficit;
    }
    private static String createMessage(String locationName, int capacity, int maxPlaces, int deficit) {
        return String.format(
                "Локация '%s' не может вместить мероприятие. " +
                        "Вместимость: %d мест, требуется: %d мест (не хватает: %d мест)",
                locationName, capacity, maxPlaces, deficit
        );
    }
}
