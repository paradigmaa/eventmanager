package school.sorokin.eventmanager.users.security.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import school.sorokin.eventmanager.users.dto.SignInUserRequestDto;
import school.sorokin.eventmanager.users.security.CustomUserDetails;

@Service
public class JwtAuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenManager jwtTokenManager;


    public JwtAuthenticationService(AuthenticationManager authenticationManager, JwtTokenManager jwtTokenManager) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenManager = jwtTokenManager;
    }

    public String authenticateUser(SignInUserRequestDto signInUserRequestDto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInUserRequestDto.login(),
                        signInUserRequestDto.password()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        Long userId = userDetails.getId();


        return jwtTokenManager.generateToken(signInUserRequestDto.login(), userId);

    }


}
