package school.sorokin.eventmanager.events.entity;

import jakarta.persistence.*;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.users.entity.UserEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String date;

    private BigDecimal cost;

    private Integer duration;

    private Integer maxPlaces;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @ManyToOne()
    @JoinColumn(name = "location_id")
    private LocationEntity location;


    @OneToMany(mappedBy = "event")
    private List<RegistrationEntity> registrations = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    public EventEntity(Long id, String name, String date, BigDecimal cost, Integer duration, Integer maxPlaces, UserEntity owner, LocationEntity location, List<RegistrationEntity> registrations, EventStatus status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.cost = cost;
        this.duration = duration;
        this.maxPlaces = maxPlaces;
        this.owner = owner;
        this.location = location;
        this.registrations = registrations;
        this.status = status;
    }

    public EventEntity() {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public void setLocation(LocationEntity location) {
        this.location = location;
    }

    public List<RegistrationEntity> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<RegistrationEntity> registrations) {
        this.registrations = registrations;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
