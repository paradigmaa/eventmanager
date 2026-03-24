package school.sorokin.eventmanager.kafka;

import org.springframework.stereotype.Component;
import school.sorokin.eventcommon.kafka.ChangeItem;
import school.sorokin.eventmanager.events.entity.EventEntity;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventComparator {

    public List<ChangeItem> compareToEvent(EventEntity oldEntity, EventEntity newEntity){
        List<ChangeItem> allChange = new ArrayList<>();
        if(!oldEntity.getName().equals(newEntity.getName())){
            allChange.add(new ChangeItem("name", oldEntity.getName(), newEntity.getName()));
        }if(!oldEntity.getMaxPlaces().equals(newEntity.getMaxPlaces())){
            allChange.add(new ChangeItem("maxPlaces", oldEntity.getMaxPlaces(), newEntity.getMaxPlaces()));
        }if(!oldEntity.getMaxPlaces().equals(newEntity.getMaxPlaces())){
            allChange.add(new ChangeItem("date", oldEntity.getDateTime(), newEntity.getDateTime()));
        }if(!oldEntity.getCost().equals(newEntity.getCost())){
            allChange.add(new ChangeItem("cost", oldEntity.getCost(), newEntity.getCost()));
        }if(!oldEntity.getDuration().equals(newEntity.getDuration())){
            allChange.add(new ChangeItem("duration", oldEntity.getDuration(), newEntity.getDuration()));
        }if(!oldEntity.getLocation().equals(newEntity.getMaxPlaces())){
            allChange.add(new ChangeItem("maxPlaces", oldEntity.getMaxPlaces(), newEntity.getMaxPlaces()));
        }
        return allChange;
    }


}
