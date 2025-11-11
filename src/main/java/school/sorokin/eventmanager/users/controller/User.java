package school.sorokin.eventmanager.users.controller;

public record User(
        Long id,

        String login,

        String password,

        Integer age
) {
}
