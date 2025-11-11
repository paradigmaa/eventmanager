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
                newLocation.getName(),
                newLocation.getAddress(),
                newLocation.getCapacity(),
                newLocation.getDescription()
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
                updateRequest.getName(),
                updateRequest.getAddress(),
                updateRequest.getCapacity(),
                updateRequest.getDescription()
        );
    }
}
