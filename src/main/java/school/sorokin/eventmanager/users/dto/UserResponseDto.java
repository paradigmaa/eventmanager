package school.sorokin.eventmanager.users.dto;


public record UserResponseDto(

        Long id,

        String login,

        String password,

        Integer age,

        String role
) {

}
