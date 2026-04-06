package school.sorokin.eventmanager.events.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.entity.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    @Column(name = "cost")
    private BigDecimal cost;

    @NotNull
    @Column(name = "duration")
    private Integer duration;

    @NotNull
    @Column(name = "max_places")
    private Integer maxPlaces;

    @ManyToOne()
    @JoinColumn(name = "owner")
    private UserEntity owner;

    @ManyToOne()
    @JoinColumn(name = "location_entity")
    private LocationEntity location;


    @OneToMany(mappedBy = "event")
    private List<RegistrationEntity> registrations = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventStatus status;
}
