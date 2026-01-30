package school.sorokin.eventmanager.events.dto;

import java.math.BigDecimal;

public record EventCreateRequestDto(

         String name,

         Integer maxPlaces,

         String date,

         BigDecimal cost,

         Integer duration,

         Integer locationId
) {

}
