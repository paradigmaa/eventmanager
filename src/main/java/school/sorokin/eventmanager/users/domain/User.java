package school.sorokin.eventmanager.users.domain;

public record User(
        Long id,

        String login,

        String password,
        Integer age) {
}
