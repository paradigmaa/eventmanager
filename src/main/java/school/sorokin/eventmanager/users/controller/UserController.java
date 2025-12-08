package school.sorokin.eventmanager.users.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sorokin.eventmanager.users.security.jwt.JwtAuthenticationService;
import school.sorokin.eventmanager.users.security.jwt.JwtTokenResponse;
import school.sorokin.eventmanager.users.security.jwt.RegistrationUserRequestDto;
import school.sorokin.eventmanager.users.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final JwtAuthenticationService jwtAuthenticationService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, JwtAuthenticationService jwtAuthenticationService) {
        this.userService = userService;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> registrationUser(@RequestBody @Valid RegistrationUserRequestDto registrationUserRequestDto) {
        UserResponseDto newUser = userService.registrationUser(registrationUserRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newUser);
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticate(
            @RequestBody @Valid SignInUserRequestDto signInUserRequestDto) {

        var token = jwtAuthenticationService.authenticateUser(signInUserRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new JwtTokenResponse(token));
    }

}
