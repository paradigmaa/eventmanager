package school.sorokin.eventmanager.users.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import school.sorokin.eventmanager.users.controller.UserResponseDto;
import school.sorokin.eventmanager.users.security.CustomUserDetailService;
import school.sorokin.eventmanager.users.service.UserService;

import java.io.IOException;
import java.util.List;


@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;

    private final CustomUserDetailService customUserDetailService;

    public JwtTokenFilter(JwtTokenManager jwtTokenManager, CustomUserDetailService customUserDetailService) {
        this.jwtTokenManager = jwtTokenManager;
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authorizationHeader.substring(7);

        if (!jwtTokenManager.validateToken(jwtToken)) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            String loginFromToken = jwtTokenManager.getLoginFromToken(jwtToken);

            UserDetails userDetails = customUserDetailService.loadUserByUsername(loginFromToken);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            return;
        }
        filterChain.doFilter(request, response);

    }
}
