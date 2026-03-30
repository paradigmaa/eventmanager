package school.sorokin.eventmanager.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.sorokin.eventcommon.kafka.ChangeItem;
import school.sorokin.eventmanager.events.entity.EventEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class EventComparator {

    public List<ChangeItem> compareToEvent(EventEntity oldEntity, EventEntity newEntity) {
        List<ChangeItem> changes = new ArrayList<>();

        log.info("=== СРАВНЕНИЕ ===");
        log.info("OLD: name={}, maxPlaces={}, dateTime={}, cost={}, duration={}, locationId={}",
                oldEntity.getName(),
                oldEntity.getMaxPlaces(),
                oldEntity.getDateTime(),
                oldEntity.getCost(),
                oldEntity.getDuration(),
                oldEntity.getLocation() != null ? oldEntity.getLocation().getId() : null
        );
        log.info("NEW: name={}, maxPlaces={}, dateTime={}, cost={}, duration={}, locationId={}",
                newEntity.getName(),
                newEntity.getMaxPlaces(),
                newEntity.getDateTime(),
                newEntity.getCost(),
                newEntity.getDuration(),
                newEntity.getLocation() != null ? newEntity.getLocation().getId() : null
        );

        // name
        if (!Objects.equals(oldEntity.getName(), newEntity.getName())) {
            changes.add(new ChangeItem("name", oldEntity.getName(), newEntity.getName()));
            log.info("✓ Изменено name");
        }

        // maxPlaces
        if (!Objects.equals(oldEntity.getMaxPlaces(), newEntity.getMaxPlaces())) {
            changes.add(new ChangeItem("maxPlaces", oldEntity.getMaxPlaces(), newEntity.getMaxPlaces()));
            log.info("✓ Изменено maxPlaces");
        }

        // dateTime
        if (!Objects.equals(oldEntity.getDateTime(), newEntity.getDateTime())) {
            changes.add(new ChangeItem("dateTime", oldEntity.getDateTime(), newEntity.getDateTime()));
            log.info("✓ Изменено dateTime");
        }

        // cost
        if (!Objects.equals(oldEntity.getCost(), newEntity.getCost())) {
            changes.add(new ChangeItem("cost", oldEntity.getCost(), newEntity.getCost()));
            log.info("✓ Изменено cost");
        }

        if (!Objects.equals(oldEntity.getDuration(), newEntity.getDuration())) {
            changes.add(new ChangeItem("duration", oldEntity.getDuration(), newEntity.getDuration()));
            log.info("✓ Изменено duration");
        }

        Long oldLocationId = oldEntity.getLocation() != null ? oldEntity.getLocation().getId() : null;
        Long newLocationId = newEntity.getLocation() != null ? newEntity.getLocation().getId() : null;

        if (!Objects.equals(oldLocationId, newLocationId)) {
            changes.add(new ChangeItem("locationId", oldLocationId, newLocationId));
            log.info("✓ Изменено location ({} → {})", oldLocationId, newLocationId);
        }

        log.info("ИТОГО ИЗМЕНЕНИЙ: {}", changes.size());
        return changes;
    }

}
