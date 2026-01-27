package school.sorokin.eventmanager.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sorokin.eventmanager.users.entity.UserEntity;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByLogin(String login);

    Optional<UserEntity> findByLogin(String username);
}
