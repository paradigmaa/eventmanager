package school.sorokin.eventmanager.events.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.events.exception.EventCapacityExceededException;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.locations.exception.NotFoundLocationException;
import school.sorokin.eventmanager.locations.repository.LocationRepository;

@Service
public class EventLocationCheckService {

    private final LocationRepository locationRepository;

    private final static Logger log = LoggerFactory.getLogger(EventLocationCheckService.class);


    public EventLocationCheckService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LocationEntity checkLocationId(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundLocationException(
                        "Локации с id=%d не существует".formatted(id)
                ));
    }

    public void checkLocationCapacity(LocationEntity newLocation, int requestedPlaces) {
        if (newLocation.getCapacity() < requestedPlaces) {
            int deficit = requestedPlaces - newLocation.getCapacity();
            log.error("Превышение capacity: {} < {}", newLocation.getCapacity(), requestedPlaces);
            throw new EventCapacityExceededException(
                    newLocation.getName(),
                    newLocation.getCapacity(),
                    requestedPlaces,
                    deficit
            );
        }
    }
}
