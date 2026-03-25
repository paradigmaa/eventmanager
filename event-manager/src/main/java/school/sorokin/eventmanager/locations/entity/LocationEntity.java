package school.sorokin.eventmanager.locations.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.sorokin.eventmanager.events.entity.EventEntity;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "location")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToMany(mappedBy = "location")
    private List<EventEntity> eventEntity = new ArrayList<>();

    public LocationEntity(Long id, String name, String address, Integer capacity, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.description = description;
    }
}
