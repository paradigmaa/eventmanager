package school.sorokin.eventmanager.locations.dto;

import org.springframework.stereotype.Component;
import school.sorokin.eventmanager.locations.domain.Location;
import school.sorokin.eventmanager.locations.entity.LocationEntity;

@Component
public class LocationConverter {

    public LocationEntity convertLocationToEntity(Location locationDomain) {
        return new LocationEntity(
                locationDomain.getId(),
                locationDomain.getName(),
                locationDomain.getAddress(),
                locationDomain.getCapacity(),
                locationDomain.getDescription()
        );
    }

    public Location convertCreateDtoToLocation(CreatLocationDto newLocation) {
        return new Location(
                null,
                newLocation.name(),
                newLocation.address(),
                newLocation.capacity(),
                newLocation.description()
        );
    }

    public ResponseLocationDto convertEntityToResponseDto(LocationEntity newLocation) {
        return new ResponseLocationDto(
                newLocation.getId(),
                newLocation.getName(),
                newLocation.getAddress(),
                newLocation.getCapacity(),
                newLocation.getDescription()
        );
    }

    public Location convertUpdateLocationDtoToLocation(UpdateLocationDto updateRequest) {
        return new Location(
                null,
                updateRequest.name(),
                updateRequest.address(),
                updateRequest.capacity(),
                updateRequest.description()
        );
    }
}
