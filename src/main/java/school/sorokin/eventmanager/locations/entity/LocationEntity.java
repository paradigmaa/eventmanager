package school.sorokin.eventmanager.locations.entity;

import jakarta.persistence.*;
import school.sorokin.eventmanager.locations.domain.LocationDomain;

@Entity
@Table(name = "location")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "description")
    private String description;

    public LocationEntity(LocationDomain locationDomain) {
        this.id = locationDomain.getId();
        this.name = locationDomain.getName();
        this.address = locationDomain.getAddress();
        this.capacity = locationDomain.getCapacity();
        this.description = locationDomain.getDescription();
    }

    public LocationEntity() {

    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public String getDescription() {
        return description;
    }
}
