package school.sorokin.eventmanager.locations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sorokin.eventmanager.locations.entity.LocationEntity;

public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer excludeId);
}
