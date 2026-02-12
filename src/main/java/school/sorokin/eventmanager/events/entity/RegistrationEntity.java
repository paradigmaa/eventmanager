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


    @Column(name = "user_id")
    private Long userId;

    public RegistrationEntity(Long id, EventEntity event, Long userId) {
        this.id = id;
        this.event = event;
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }



}

