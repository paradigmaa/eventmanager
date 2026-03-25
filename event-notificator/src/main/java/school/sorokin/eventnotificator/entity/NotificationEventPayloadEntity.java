package school.sorokin.eventnotificator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationEventPayloadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payloadId;
    private UUID messageId;
    private String eventType;
    private Long eventId;

    @CreationTimestamp
    private LocalDateTime occurredAt;
    private Long changedById;
    private Long ownerId;

    @Column(columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

}
