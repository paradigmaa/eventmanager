package school.sorokin.eventmanager.locations.dto;

import java.time.LocalDateTime;

public class ServerErrorDto {
    String message;

    String detail;

    LocalDateTime localDateTime;

    public ServerErrorDto(String message, String detail, LocalDateTime localDateTime) {
        this.message = message;
        this.detail = detail;
        this.localDateTime = localDateTime;
    }
}
