package school.sorokin.eventmanager.users.controller;


public record UserResponseDto(

        Long id,

        String login,

        String password,

        Integer age,

        String role
) {

}
