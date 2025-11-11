package school.sorokin.eventmanager.users.service;


import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.users.UserConverter;
import school.sorokin.eventmanager.users.controller.RegistrationUserDto;
import school.sorokin.eventmanager.users.controller.UserResponseDto;
import school.sorokin.eventmanager.users.controller.User;
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

    public UserResponseDto registrationUser(RegistrationUserDto registrationUserDto) {
        if(userRepository.existsByLogin(registrationUserDto.login())){
            throw new LoginTakenNameException("Такой логин уже существует");
        }
        User newUser = userConverter.convertRegistrationUserDtoToUser(registrationUserDto);
        UserEntity saveUser = userRepository.save(userConverter.convertUserToUserEntity(newUser));
        return userConverter.convertUserEntityToUserResponseDto(saveUser);
    }
}
