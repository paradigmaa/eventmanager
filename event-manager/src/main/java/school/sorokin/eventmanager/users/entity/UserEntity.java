package school.sorokin.eventmanager.users.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.RegistrationEntity;
import school.sorokin.eventmanager.users.dto.RoleUsers;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank
    private String login;

    @NotBlank
    private String passwordHash;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleUsers role;

    @OneToMany(mappedBy = "owner")
    private List<EventEntity> ownedEvents  = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<RegistrationEntity> registrations = new ArrayList<>();

    public UserEntity(Long id, String login, String passwordHash, Integer age, RoleUsers role) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.age = age;
        this.role = role;
    }
}
