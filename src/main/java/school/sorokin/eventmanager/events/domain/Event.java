package school.sorokin.eventmanager.events.domain;

import java.math.BigDecimal;

public record Event(
        String name,

        Integer maxPlaces,

        String date,

        BigDecimal cost,

        Integer duration,

        Long locationId
) {
}
