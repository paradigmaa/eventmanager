package school.sorokin.eventmanager.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventCreateRequestDto(
        @NotBlank(message = "Имя не может быть пустым")
        @Size(min = 1, max = 255, message = "Название должно быть от 1 до 255 символов")
        String name,
        @Size(min = 1, max = 100000)
        @Positive(message = "Нельзя ставить отрицательные значения")
        Integer maxPlaces,
        @NotNull(message = "Дата и время обязательны")
        @Future(message = "Дата мероприятия должна быть в будущем")
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        LocalDateTime date,
        @NotNull(message = "Стоимость обязательна")
        @Positive(message = "Нельзя ставить отрицательные значения")
        BigDecimal cost,
        @NotNull(message = "Длительность обязательна")
        @Positive(message = "Нельзя ставить отрицательные значения")
        Integer duration,
        @NotNull(message = "Локация обязательна")
        @Positive(message = "ID локации должен быть положительным")
        Long locationId
) {

}
