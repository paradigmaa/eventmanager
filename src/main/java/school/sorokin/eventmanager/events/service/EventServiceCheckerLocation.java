package school.sorokin.eventmanager.events.service;

import org.springframework.stereotype.Component;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.locations.exception.NotFoundLocationException;
import school.sorokin.eventmanager.locations.repository.LocationRepository;

@Component
public class EventServiceCheckerLocation {

    private final LocationRepository locationRepository;


    public EventServiceCheckerLocation(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LocationEntity checkLocationId(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundLocationException(
                        "Локации с id=%d не существует".formatted(id)
                ));
    }
}
