package school.sorokin.eventmanager.events.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.locations.repository.LocationRepository;
import school.sorokin.eventmanager.users.entity.UserEntity;
import school.sorokin.eventmanager.users.exception.UserNotFoundException;
import school.sorokin.eventmanager.users.repository.UserRepository;
import school.sorokin.eventmanager.users.security.CustomUserDetails;

@Service
public class EventServiceCheckerUser {

    private final UserRepository userRepository;

    private final LocationRepository locationRepository;

    public EventServiceCheckerUser(UserRepository userRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    public UserEntity checkToFindUser() {
        Long ownerId = ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()).getId();

        return userRepository.findById(ownerId).orElseThrow(() ->
                new UserNotFoundException("Пользователя с id=%d не существует".formatted(ownerId)));

    }
}
