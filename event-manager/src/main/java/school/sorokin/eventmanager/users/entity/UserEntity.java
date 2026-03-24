package school.sorokin.eventmanager.users.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import school.sorokin.eventmanager.events.entity.EventEntity;
import school.sorokin.eventmanager.events.entity.RegistrationEntity;
import school.sorokin.eventmanager.users.dto.RoleUsers;

import java.util.ArrayList;
import java.util.List;

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

    public UserEntity() {

    }

    public UserEntity(Long id, String login, String passwordHash, Integer age, RoleUsers role, List<EventEntity> ownedEvents, List<RegistrationEntity> registrations) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.age = age;
        this.role = role;
        this.ownedEvents = ownedEvents;
        this.registrations = registrations;
    }

    public UserEntity(Long id, String login, String passwordHash, Integer age, RoleUsers role) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.age = age;
        this.role = role;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleUsers getRole() {
        return role;
    }

    public void setRole(RoleUsers role) {
        this.role = role;
    }

    public List<EventEntity> getOwnedEvents() {
        return ownedEvents;
    }

    public void setOwnedEvents(List<EventEntity> ownedEvents) {
        this.ownedEvents = ownedEvents;
    }

    public List<RegistrationEntity> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<RegistrationEntity> registrations) {
        this.registrations = registrations;
    }
}
