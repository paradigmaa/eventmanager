package school.sorokin.eventmanager.locations.dto;

import org.springframework.stereotype.Component;
import school.sorokin.eventmanager.locations.domain.LocationDomain;

@Component
public class ConverterToDto {

    public LocationDto convertToDto(LocationDomain locationDomain) {
        return new LocationDto(locationDomain);
    }

    public LocationDomain convertToDomain(LocationDto locationDto) {
        return new LocationDomain(locationDto);
    }
}
