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

    @Column(name = "adress")
    private String address;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "description")
    private String description;

    public LocationEntity(Integer id, String name, String address, Integer capacity, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.description = description;
    }

    public LocationEntity(LocationDomain locationDomain) {
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

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
