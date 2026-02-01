package school.sorokin.eventmanager.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sorokin.eventmanager.events.entity.EventEntity;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Integer> {

}
