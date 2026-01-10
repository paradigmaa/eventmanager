package school.sorokin.eventmanager.users.dto;


public record UserResponseDto(

        Long id,

        String login,

        Integer age,

        String role
) {

}
