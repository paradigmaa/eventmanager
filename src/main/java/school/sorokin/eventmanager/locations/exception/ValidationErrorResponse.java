package school.sorokin.eventmanager.locations.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ValidationErrorResponse {
    private String message;
    private List<FieldError> errors;
    private LocalDateTime timestamp;

    public ValidationErrorResponse(String message, List<FieldError> errors, LocalDateTime timestamp) {
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
