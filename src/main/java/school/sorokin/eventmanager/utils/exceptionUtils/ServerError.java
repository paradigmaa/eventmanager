package school.sorokin.eventmanager.utils.exceptionUtils;

import java.time.LocalDateTime;

public record ServerError(String message, String detailMessage, LocalDateTime timestamp) {
}
