package school.sorokin.eventmanager.globalExceptionHandler.exceptionUtils;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(String message,
                                      List<FieldError> errors,
                                      LocalDateTime timestamp) {
}
