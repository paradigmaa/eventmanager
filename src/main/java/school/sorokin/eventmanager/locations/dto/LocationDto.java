package school.sorokin.eventmanager.locations.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LocationDto {
    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotNull
    @Min(5)
    private Integer capacity;

    @NotBlank
    private String description;
}
