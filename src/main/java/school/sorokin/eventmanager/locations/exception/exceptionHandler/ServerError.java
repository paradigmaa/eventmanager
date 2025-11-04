package school.sorokin.eventmanager.locations.exception.exceptionHandler;

import java.time.LocalDateTime;

public record ServerError(String message, String detailMessage, LocalDateTime timestamp) {
}
