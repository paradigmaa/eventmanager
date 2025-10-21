package school.sorokin.eventmanager.locations.domain;
import school.sorokin.eventmanager.locations.dto.LocationDto;
import school.sorokin.eventmanager.locations.entity.LocationEntity;

public class LocationDomain {

    private Integer id;

    private String name;

    private String address;

    private Integer capacity;

    private String description;

    public LocationDomain(LocationDomain locationDomain) {
        this.id = locationDomain.getId();
        this.name = locationDomain.getName();
        this.address = locationDomain.getAddress();
        this.capacity = locationDomain.getCapacity();
        this.description = locationDomain.getDescription();
    }

    public LocationDomain(LocationEntity locationEntity) {
        this.id = locationEntity.getId();
        this.name = locationEntity.getName();
        this.address = locationEntity.getAddress();
        this.capacity = locationEntity.getCapacity();
        this.description = locationEntity.getDescription();
    }

    public LocationDomain(LocationDto locationDto){
        this.id = locationDto.getId();
        this.name = locationDto.getName();
        this.address = locationDto.getAddress();
        this.capacity = locationDto.getCapacity();
        this.description = locationDto.getDescription();
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
