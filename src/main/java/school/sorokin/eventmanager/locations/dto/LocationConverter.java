package school.sorokin.eventmanager.locations.dto;

import org.springframework.stereotype.Component;
import school.sorokin.eventmanager.locations.domain.Location;
import school.sorokin.eventmanager.locations.entity.LocationEntity;

@Component
public class LocationConverter {

    public LocationEntity convertToEntity(Location locationDomain) {
        return new LocationEntity(
                locationDomain.getId(),
                locationDomain.getName(),
                locationDomain.getAddress(),
                locationDomain.getCapacity(),
                locationDomain.getDescription()
        );
    }

    public Location convertToDomain(LocationEntity locationEntity) {
        return new Location(
                locationEntity.getId(),
                locationEntity.getName(),
                locationEntity.getAddress(),
                locationEntity.getCapacity(),
                locationEntity.getDescription()
        );
    }
}
