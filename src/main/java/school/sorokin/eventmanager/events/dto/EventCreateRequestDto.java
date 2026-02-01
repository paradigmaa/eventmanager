package school.sorokin.eventmanager.events.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EventCreateRequestDto(

        @NotBlank
        String name,
        @NotNull
        Integer maxPlaces,
        @NotBlank
        String date,
        @NotNull
        BigDecimal cost,
        @NotNull
        Integer duration,
        @NotNull
        Long locationId
) {

}
