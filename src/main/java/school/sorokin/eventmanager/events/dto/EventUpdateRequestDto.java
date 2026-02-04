package school.sorokin.eventmanager.events.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventUpdateRequestDto(
        @NotBlank
        String name,
        @NotNull
        Integer maxPlaces,
        @NotBlank
        LocalDateTime dateTime,
        @NotNull
        BigDecimal cost,
        @NotNull
        Integer duration,
        @NotNull
        Long locationId
) {
}
