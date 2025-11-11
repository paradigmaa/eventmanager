package school.sorokin.eventmanager.locations.exception.exceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(String message, List<FieldError> errors, LocalDateTime timestamp) {
}
