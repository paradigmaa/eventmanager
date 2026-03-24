package school.sorokin.eventmanager.events.entity;

import jakarta.persistence.*;
import school.sorokin.eventmanager.users.entity.UserEntity;

@Entity
public class RegistrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public RegistrationEntity(Long id, EventEntity event, UserEntity user) {
        this.id = id;
        this.event = event;
        this.user = user;
    }

    public RegistrationEntity() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}

