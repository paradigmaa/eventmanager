package school.sorokin.eventmanager.locations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sorokin.eventmanager.locations.entity.LocationEntity;


@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer excludeId);
}
