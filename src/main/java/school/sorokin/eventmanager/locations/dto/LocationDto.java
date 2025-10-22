package school.sorokin.eventmanager.locations.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import school.sorokin.eventmanager.locations.domain.LocationDomain;

public class LocationDto {

    @Null
    private Integer id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Локация должна иметь адрес")
    private String address;

    @NotNull
    @Min(value = 5, message = "не должно быть меньше 5")
    private Integer capacity;

    @NotBlank
    private String description;

    public LocationDto(LocationDomain locationDomain) {
        this.id = locationDomain.getId();
        this.name = locationDomain.getName();
        this.address = locationDomain.getAddress();
        this.capacity = locationDomain.getCapacity();
        this.description = locationDomain.getDescription();
    }

    public LocationDto() {

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
