package school.sorokin.eventmanager.users.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrationUserDto(

        @NotBlank
        @Size(min = 2, max = 20)
        String login,

        @NotBlank
        @Min(5)
        String password,

        @NotNull
        Integer age
) {
}
