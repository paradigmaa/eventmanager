package school.sorokin.eventmanager.users.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    private String roleUsers;

    public UserEntity() {

    }

    public UserEntity(Long id, String login, String passwordHash, String roleUsers) {
        this.id = id;
        this.login = login;
        this.roleUsers = roleUsers;
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

    public String getRoleUsers() {
        return roleUsers;
    }

    public void setRoleUsers(String roleUsers) {
        this.roleUsers = roleUsers;
    }
}
