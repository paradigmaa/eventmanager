package school.sorokin.eventmanager.globalExceptionHandler.exceptionUtils;

import java.time.LocalDateTime;

public record ServerError(String message, String detailMessage, LocalDateTime timestamp) {
}
