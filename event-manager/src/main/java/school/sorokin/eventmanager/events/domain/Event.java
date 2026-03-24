package school.sorokin.eventmanager.events.domain;

import school.sorokin.eventmanager.events.entity.EventStatus;
import school.sorokin.eventmanager.events.entity.RegistrationEntity;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.entity.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Event(
        Long id,

        String name,

        LocalDateTime dateTime,

        BigDecimal cost,

        Integer duration,

        Integer maxPlaces,

        UserEntity owner,

        LocationEntity location,

        List<RegistrationEntity> registrations,

        EventStatus status
) {
}
