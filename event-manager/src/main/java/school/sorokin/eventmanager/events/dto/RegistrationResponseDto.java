package school.sorokin.eventmanager.events.dto;

import school.sorokin.eventmanager.events.entity.EventEntity;

public record RegistrationResponseDto(
        Long id,

        String eventName,

        String userName
) {
}
