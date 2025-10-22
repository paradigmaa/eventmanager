package school.sorokin.eventmanager.locations.entity;

import org.springframework.stereotype.Component;
import school.sorokin.eventmanager.locations.domain.LocationDomain;

@Component
public class ConverterToEntity {

    public LocationEntity convertToEntity(LocationDomain locationDomain) {
        return new LocationEntity(locationDomain);
    }

    public LocationDomain convertToDomain(LocationEntity locationEntity) {
        return new LocationDomain(locationEntity);
    }
}
