package school.sorokin.eventmanager.events.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventCreateRequestDto(
        @NotBlank(message = "Имя не может быть пустым")
        @Size(min = 1, max = 255, message = "Название должно быть от 1 до 255 символов")
        String name,
        @NotNull(message = "Минимальное значение 0")
        Integer maxPlaces,
        @NotNull(message = "Дата и время обязательны")
        @Future(message = "Дата мероприятия должна быть в будущем")
        LocalDateTime dateTime,
        @NotNull(message = "Стоимость обязательна")
        BigDecimal cost,
        @NotNull(message = "Длительность обязательна")
        Integer duration,
        @NotNull(message = "Локация обязательна")
        @Positive(message = "ID локации должен быть положительным")
        Long locationId
) {

}
