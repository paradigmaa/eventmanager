package school.sorokin.eventmanager.events.dto;

public record EventRegistrationRequestDto(
        Long userId,

        Long eventId
) {
}
