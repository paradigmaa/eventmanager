package school.sorokin.eventmanager.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sorokin.eventmanager.users.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> registrationUser(@RequestBody RegistrationUserDto registrationUserDto){
        UserResponseDto newUser = userService.registrationUser(registrationUserDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newUser);
    }
}
