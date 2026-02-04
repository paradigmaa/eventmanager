package school.sorokin.eventmanager.events.eventsUtils;

import school.sorokin.eventmanager.events.entity.EventStatus;

import java.math.BigDecimal;

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
