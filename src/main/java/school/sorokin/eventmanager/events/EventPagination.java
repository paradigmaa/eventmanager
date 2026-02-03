package school.sorokin.eventmanager.events;

import school.sorokin.eventmanager.events.entity.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventPagination(
        String name,
        Integer placesMin,
        Integer placesMax,
        String dateStartAfter,
        String dateStartBefore,
        BigDecimal costMin,
        BigDecimal costMax,
        Integer durationMin,
        Integer durationMax,
        Long locationId,
        EventStatus eventStatus,
        Integer pageNumber,
        Integer pageSize

) {
    public EventPagination {
        if(pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 20;
    }
}
