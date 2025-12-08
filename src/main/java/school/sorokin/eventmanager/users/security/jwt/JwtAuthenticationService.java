package school.sorokin.eventmanager.users.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.users.controller.SignInUserRequestDto;

@Service
public class JwtAuthenticationService {

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final JwtTokenManager jwtTokenManager;


    @Autowired
    public JwtAuthenticationService(AuthenticationManager authenticationManager, JwtTokenManager jwtTokenManager) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenManager = jwtTokenManager;
    }

    public String authenticateUser(SignInUserRequestDto signInUserRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInUserRequestDto.login(),
                        signInUserRequestDto.password()
                )
        );
        return jwtTokenManager.generateToken(signInUserRequestDto.login());

    }
}
