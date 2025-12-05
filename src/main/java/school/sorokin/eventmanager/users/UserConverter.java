package school.sorokin.eventmanager.users;

import org.springframework.stereotype.Component;
import school.sorokin.eventmanager.users.controller.RegistrationUserRequestDto;
import school.sorokin.eventmanager.users.controller.UserResponseDto;
import school.sorokin.eventmanager.users.controller.User;
import school.sorokin.eventmanager.users.dto.RoleUsers;
import school.sorokin.eventmanager.users.entity.UserEntity;

@Component
public class UserConverter {
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
                newUser.password(),
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
