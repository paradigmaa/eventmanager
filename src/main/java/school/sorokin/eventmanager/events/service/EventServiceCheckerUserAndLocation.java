package school.sorokin.eventmanager.events.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.locations.exception.NotFoundLocationException;
import school.sorokin.eventmanager.locations.repository.LocationRepository;
import school.sorokin.eventmanager.locations.service.LocationService;
import school.sorokin.eventmanager.users.entity.UserEntity;
import school.sorokin.eventmanager.users.exception.UserNotFoundException;
import school.sorokin.eventmanager.users.repository.UserRepository;
import school.sorokin.eventmanager.users.security.CustomUserDetails;
import school.sorokin.eventmanager.users.service.UserService;

@Service
public class EventServiceCheckerUserAndLocation {

    private final UserRepository userRepository;

    private final LocationRepository locationRepository;

    public EventServiceCheckerUserAndLocation(UserRepository userRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    public UserEntity checkToFindUser(){
        Long ownerId = ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()).getId();

        return userRepository.findById(ownerId).orElseThrow(() ->
                new UserNotFoundException("Пользователя с id=%d не существует".formatted(ownerId)));

    }

    public LocationEntity checkLocationId(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(
                        () -> {
                            return new NotFoundLocationException(
                                    "Локации с id=%d не существует"
                                            .formatted(id));
                        });
    }

}
