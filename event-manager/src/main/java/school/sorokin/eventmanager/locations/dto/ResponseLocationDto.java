package school.sorokin.eventmanager.locations.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResponseLocationDto(
        Long id,

        String name,

        String address,

        Integer capacity,

        String description
) {

}
