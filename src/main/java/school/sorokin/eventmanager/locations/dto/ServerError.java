package school.sorokin.eventmanager.locations.dto;

import java.time.LocalDateTime;

public class ServerError {
    private String message;

    private String detailMessage;

    private LocalDateTime timestamp;

    public ServerError(String message, LocalDateTime timestamp) {
        this.message = message;

        this.timestamp = timestamp;
    }

    public ServerError(String message, String detailMessage, LocalDateTime timestamp) {
        this.message = message;
        this.detailMessage = detailMessage;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
