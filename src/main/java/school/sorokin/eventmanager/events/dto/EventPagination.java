package school.sorokin.eventmanager.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import school.sorokin.eventmanager.events.entity.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventPagination(
        String name,
        Integer placesMin,
        Integer placesMax,
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        LocalDateTime dateStartAfter,
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        LocalDateTime dateStartBefore,
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
