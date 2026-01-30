package school.sorokin.eventmanager.events.entity;

import jakarta.persistence.*;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.entity.UserEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String date;

    private BigDecimal cost;

    private Integer duration;

    private Integer maxPlaces;


    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @ManyToOne()
    @JoinColumn(name = "location_id")
    private LocationEntity location;


    @OneToMany(mappedBy = "event")
    private List<RegistrationEntity> registrations = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventStatus status;

}
