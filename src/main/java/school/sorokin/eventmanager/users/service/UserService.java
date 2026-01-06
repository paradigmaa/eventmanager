package school.sorokin.eventmanager.users.service;

import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.users.UserConverter;
import school.sorokin.eventmanager.users.exception.UserNotFoundException;
import school.sorokin.eventmanager.users.security.jwt.RegistrationUserRequestDto;
import school.sorokin.eventmanager.users.dto.UserResponseDto;
import school.sorokin.eventmanager.users.domain.User;
import school.sorokin.eventmanager.users.entity.UserEntity;
import school.sorokin.eventmanager.users.exception.LoginTakenNameException;
import school.sorokin.eventmanager.users.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserConverter userConverter;

    public UserService(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    public UserResponseDto registrationUser(RegistrationUserRequestDto registrationUserRequestDto) {
        if (userRepository.existsByLogin(registrationUserRequestDto.login())) {
            throw new LoginTakenNameException("Такой логин уже существует");
        }
        User newUser = userConverter.convertRegistrationUserDtoToUser(registrationUserRequestDto);
        UserEntity saveUser = userRepository.save(userConverter.convertUserToUserEntity(newUser));
        return userConverter.convertUserEntityToUserResponseDto(saveUser);
    }

    public UserResponseDto findUserById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Пользователя с id=%d не существует".formatted(id)));
        return userConverter.convertUserEntityToUserResponseDto(user);
    }
}
