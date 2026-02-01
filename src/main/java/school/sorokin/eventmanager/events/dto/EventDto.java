package school.sorokin.eventmanager.events.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventDto
        (
                Long id,
                String name,
                Long ownerId,
                Integer maxPlaces,
                Integer occupiedPlaces,
                String date,
                BigDecimal cost,
                Integer duration,
                Long locationId,
                Enum status
        ){
}
