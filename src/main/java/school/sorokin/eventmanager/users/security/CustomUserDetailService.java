package school.sorokin.eventmanager.users.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import school.sorokin.eventmanager.users.entity.UserEntity;
import school.sorokin.eventmanager.users.repository.UserRepository;

@Component
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;


    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user =
                userRepository.findByLogin(username)
                        .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        return User.withUsername(username)
                .password(user.getPassword())
                .authorities(user.getRoleUsers())
                .build();
    };
}
