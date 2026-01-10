package school.sorokin.eventmanager.users.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventmanager.users.UserConverter;
import school.sorokin.eventmanager.users.exception.UserNotFoundException;
import school.sorokin.eventmanager.users.dto.RegistrationUserRequestDto;
import school.sorokin.eventmanager.users.dto.UserResponseDto;
import school.sorokin.eventmanager.users.domain.User;
import school.sorokin.eventmanager.users.entity.UserEntity;
import school.sorokin.eventmanager.users.exception.LoginTakenNameException;
import school.sorokin.eventmanager.users.repository.UserRepository;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final UserConverter userConverter;

    public UserService(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @Transactional
    public UserResponseDto registrationUser(RegistrationUserRequestDto registrationUserRequestDto) {
        if (userRepository.existsByLogin(registrationUserRequestDto.login())) {
            throw new LoginTakenNameException("Такой логин уже существует");
        }
        User newUser = userConverter.convertRegistrationUserDtoToUser(registrationUserRequestDto);
        UserEntity saveUser = userRepository.save(userConverter.convertUserToUserEntity(newUser));
        return userConverter.convertUserEntityToUserResponseDto(saveUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findUserById(Long id) {
        log.info("Запрос на поиск пользователя");
        UserEntity user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Пользователя с id=%d не существует".formatted(id)));
        log.info("Запрос на поиск пользователя завершен");
        return userConverter.convertUserEntityToUserResponseDto(user);
    }
}
