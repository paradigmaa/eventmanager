package school.sorokin.eventmanager.events.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.locations.repository.LocationRepository;
import school.sorokin.eventmanager.users.entity.UserEntity;
import school.sorokin.eventmanager.users.exception.UserNotFoundException;
import school.sorokin.eventmanager.users.repository.UserRepository;
import school.sorokin.eventmanager.users.security.CustomUserDetails;

@Service
public class EventUserCheckService {

    private final UserRepository userRepository;

    public EventUserCheckService(UserRepository userRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity checkToFindUser() {
        Long ownerId = ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()).getId();

        return userRepository.findById(ownerId).orElseThrow(() ->
                new UserNotFoundException("Пользователя с id=%d не существует".formatted(ownerId)));

    }
}
