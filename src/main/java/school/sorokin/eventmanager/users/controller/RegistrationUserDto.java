package school.sorokin.eventmanager.users.controller;

public record RegistrationUserDto(
         String login,

         String password,

         Integer age
) {
}
