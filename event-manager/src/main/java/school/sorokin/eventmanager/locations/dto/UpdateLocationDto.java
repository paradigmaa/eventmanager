package school.sorokin.eventmanager.locations.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateLocationDto(
    @NotBlank(message = "Имя не может быть пустым")
    String name,

    @NotBlank(message = "Локация должна иметь адрес")
    String address,

    @NotNull
    @Min(value = 5, message = "не должно быть меньше 5")
    @Max(value = 1000000, message = "не должно превышать 1 млн")
    Integer capacity,

    @NotBlank(message = "описание не должно быть пустым")
    String description
){

}
