package school.sorokin.eventmanager.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventUpdateRequestDto(
        @NotBlank(message = "Имя не может быть пустым")
        String name,
        @NotNull(message = "Количество мест не может быть пустым")
        @Size(min = 1, max = 100000)
        Integer maxPlaces,
        @NotBlank(message = "Дата не может быть без значения или в прошлом")
        @Future(message = "Дата мероприятия должна быть в будущем")
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        LocalDateTime date,
        @NotNull(message = "Стоимость не может быть 0")
        @Positive(message = "Нельзя ставить отрицательные значения")
        BigDecimal cost,
        @NotNull(message = "Продолжительность не может быть 0")
        @Positive(message = "Нельзя ставить отрицательные значения")
        Integer duration,
        @NotNull(message = "Локация не может быть пустой")
        @Positive(message = "Нельзя ставить отрицательные значения")
        Long locationId
) {
}
