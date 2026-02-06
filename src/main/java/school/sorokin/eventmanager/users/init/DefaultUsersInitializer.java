package school.sorokin.eventmanager.users.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventmanager.users.dto.RoleUsers;
import school.sorokin.eventmanager.users.entity.UserEntity;
import school.sorokin.eventmanager.users.repository.UserRepository;


@Component
public class DefaultUsersInitializer {

    private static final Logger log = LoggerFactory.getLogger(DefaultUsersInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_ADMIN_LOGIN = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin";
    private static final String DEFAULT_USER_LOGIN = "user";
    private static final String DEFAULT_USER_PASSWORD = "user";

    public DefaultUsersInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initDefaultUsers() {
        log.info("Starting initialization of default users...");

        initAdminUser();
        initRegularUser();

        log.info("Default users initialization completed");
    }

    private void initAdminUser() {
        if (userRepository.existsByLogin(DEFAULT_ADMIN_LOGIN)) {
            log.info("Admin user already exists, skipping creation");
            return;
        }

        UserEntity admin = new UserEntity();
        admin.setLogin(DEFAULT_ADMIN_LOGIN);
        admin.setPasswordHash(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
        admin.setRole(RoleUsers.ADMIN);
        admin.setAge(30);

        userRepository.save(admin);
        log.info("Created default admin user with login: {}", DEFAULT_ADMIN_LOGIN);
    }

    private void initRegularUser() {
        if (userRepository.existsByLogin(DEFAULT_USER_LOGIN)) {
            log.info("Regular user already exists, skipping creation");
            return;
        }

        UserEntity user = new UserEntity();
        user.setLogin(DEFAULT_USER_LOGIN);
        user.setPasswordHash(passwordEncoder.encode(DEFAULT_USER_PASSWORD));
        user.setRole(RoleUsers.USER);
        user.setAge(25);

        userRepository.save(user);
        log.info("Created default regular user with login: {}", DEFAULT_USER_LOGIN);
    }
}
