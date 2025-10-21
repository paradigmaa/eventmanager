package school.sorokin.eventmanager.locations.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.sorokin.eventmanager.locations.entity.LocationEntity;

public class LocationDomain {

    private String name;

    private String address;

    private Integer capacity;

    private String description;

    public LocationDomain() {
    }

    public LocationDomain(LocationDomain locationDomain) {
        this.name = locationDomain.getName();
        this.address = locationDomain.address;
        this.capacity = locationDomain.getCapacity();
        this.description = locationDomain.getDescription();
    }

    public LocationDomain(LocationEntity locationEntity) {
        this.name = locationEntity.getName();
        this.address = locationEntity.getAddress();
        this.capacity = locationEntity.getCapacity();
        this.description = locationEntity.getDescription();
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
