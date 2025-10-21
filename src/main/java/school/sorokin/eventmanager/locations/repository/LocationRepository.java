package school.sorokin.eventmanager.locations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventmanager.locations.domain.LocationDomain;
import school.sorokin.eventmanager.locations.entity.LocationEntity;

public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {


    @Modifying
    @Transactional
    @Query("""
    UPDATE LocationEntity l SET l.name = :#{#locationDomain.name},
        l.address = :#{#locationDomain.address},
            l.capacity = :#{#locationDomain.capacity},
                l.description = :#{#locationDomain.description}
    WHERE l.id = :id
    """)
    LocationEntity updateLocation(@Param("id") Integer id,
                        @Param("locationDomain") LocationDomain locationDomain);
}
