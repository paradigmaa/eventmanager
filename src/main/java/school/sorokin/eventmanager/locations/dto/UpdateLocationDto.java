package school.sorokin.eventmanager.locations.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateLocationDto {
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Локация должна иметь адрес")
    private String address;

    @NotNull
    @Min(value = 5, message = "не должно быть меньше 5")
    @Max(value = 1000000, message = "не должно превышать 1 млн")
    private Integer capacity;

    @NotBlank(message = "описание не должно быть пустым")
    private String description;

    public UpdateLocationDto() {

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
