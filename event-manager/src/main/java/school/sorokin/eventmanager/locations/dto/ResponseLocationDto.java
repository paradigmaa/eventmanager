package school.sorokin.eventmanager.locations.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResponseLocationDto {
    private Long id;

    private String name;

    private String address;

    private Integer capacity;

    private String description;

    public ResponseLocationDto() {

    }

    public ResponseLocationDto(Long id, String name, String address, Integer capacity, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
