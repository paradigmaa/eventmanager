package school.sorokin.eventmanager.users.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.sorokin.eventmanager.users.dto.RoleUsers;

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
    private String password;

    @NotNull
    private Integer age;

    private String roleUsers;

    public UserEntity() {

    }

    public UserEntity(Long id, String login, String password, Integer age, String roleUsers) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.age = age;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRoleUsers() {
        return roleUsers;
    }

    public void setRoleUsers(String roleUsers) {
        this.roleUsers = roleUsers;
    }
}
