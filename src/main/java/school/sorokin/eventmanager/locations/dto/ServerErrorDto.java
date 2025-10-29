package school.sorokin.eventmanager.locations.dto;

import java.time.LocalDateTime;

public class ServerErrorDto {
    private String message;

    private String detail;

    private LocalDateTime localDateTime;

    public ServerErrorDto(String message, String detail, LocalDateTime localDateTime) {
        this.message = message;
        this.detail = detail;
        this.localDateTime = localDateTime;
    }

    public ServerErrorDto() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }


}
