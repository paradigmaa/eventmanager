package school.sorokin.eventmanager.locations.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "location")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    @NotBlank(message = "ия не должно быть пустым")
    private String name;

    @Column(name = "address")
    @NotBlank(message = "локация должна иметь адрес")
    private String address;

    @Column(name = "capacity")
    @NotNull(message = "Вместимость не может быть < 0")
    @Min(value = 5, message = "не должно быть меньше 5")
    @Max(value = 1000000, message = "не должно превышать 1 млн")
    private Integer capacity;

    @Column(name = "description")
    @NotBlank(message = "описание не может быть пустым")
    private String description;

    public LocationEntity(Integer id, String name, String address, Integer capacity, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.description = description;
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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
