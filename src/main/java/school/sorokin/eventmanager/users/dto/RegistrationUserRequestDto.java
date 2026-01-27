package school.sorokin.eventmanager.users.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationUserRequestDto(

        @NotBlank
        @Size(min = 2, max = 20)
        String login,

        @NotBlank
        @Size(min= 5)
        String password,

        Integer age
) {
}
