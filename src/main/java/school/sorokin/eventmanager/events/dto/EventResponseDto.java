package school.sorokin.eventmanager.events.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponseDto
        (
                Long id,
                String name,
                Long ownerId,
                Integer maxPlaces,
                Integer occupiedPlaces,
                LocalDateTime date,
                BigDecimal cost,
                Integer duration,
                Long locationId,
                Enum status
        ){
}
