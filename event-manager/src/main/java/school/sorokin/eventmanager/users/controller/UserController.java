package school.sorokin.eventmanager.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.sorokin.eventmanager.users.dto.SignInUserRequestDto;
import school.sorokin.eventmanager.users.dto.UserResponseDto;
import school.sorokin.eventmanager.users.security.jwt.JwtAuthenticationService;
import school.sorokin.eventmanager.users.dto.JwtTokenResponse;
import school.sorokin.eventmanager.users.dto.RegistrationUserRequestDto;
import school.sorokin.eventmanager.users.service.UserService;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtAuthenticationService jwtAuthenticationService;

    @PostMapping
    public ResponseEntity<UserResponseDto> registrationUser(@RequestBody @Valid RegistrationUserRequestDto registrationUserRequestDto) {
        log.info("POST /users - регистрация пользователя: '{}'", registrationUserRequestDto.login());
        UserResponseDto newUser = userService.registrationUser(registrationUserRequestDto);
        log.info("POST /users - регистрация пользователя: '{}' завершена", newUser.login());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newUser);
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticate(
            @RequestBody @Valid SignInUserRequestDto signInUserRequestDto) {
        log.info("POST /users/auth - аутентификация пользователя: '{}'", signInUserRequestDto.login());
        var token = jwtAuthenticationService.authenticateUser(signInUserRequestDto);
        log.info("POST /users/auth - аутентификация пользователя завершена: '{}'", signInUserRequestDto.login());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new JwtTokenResponse(token));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable("id") Long id) {
        log.info("GET /users/{id} - поиск пользователя по id: '{}'", id);
        UserResponseDto findUser = userService.findUserById(id);
        return ResponseEntity.ok().body(findUser);
    }
}
