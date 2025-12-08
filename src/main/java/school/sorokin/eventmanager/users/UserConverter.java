package school.sorokin.eventmanager.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import school.sorokin.eventmanager.users.security.jwt.RegistrationUserRequestDto;
import school.sorokin.eventmanager.users.controller.UserResponseDto;
import school.sorokin.eventmanager.users.controller.User;
import school.sorokin.eventmanager.users.dto.RoleUsers;
import school.sorokin.eventmanager.users.entity.UserEntity;

@Component
public class UserConverter {

    private final PasswordEncoder passwordEncoder;


    public UserConverter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User convertRegistrationUserDtoToUser(RegistrationUserRequestDto registrationUserRequestDto) {
        return new User(
                null,
                registrationUserRequestDto.login(),
                registrationUserRequestDto.password(),
                registrationUserRequestDto.age()
        );
    }

    public UserEntity convertUserToUserEntity(User newUser) {
        return new UserEntity(
                null,
                newUser.login(),
                passwordEncoder.encode(newUser.password()),
                newUser.age(),
                RoleUsers.USER.toString()
        );
    }

    public UserResponseDto convertUserEntityToUserResponseDto(UserEntity saveUser) {
        return new UserResponseDto(
                saveUser.getId(),
                saveUser.getLogin(),
                saveUser.getPassword(),
                saveUser.getAge(),
                saveUser.getRoleUsers()
        );
    }
}
