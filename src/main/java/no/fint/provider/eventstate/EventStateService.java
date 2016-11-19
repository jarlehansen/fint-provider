package no.fint.provider.eventstate;

import no.fint.event.model.Event;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EventStateService {

    private Map<String, EventState> eventStateMap;

    public EventStateService() {
        eventStateMap = new HashMap<>();
    }

    public boolean exists(Event event) {
        if (eventStateMap.containsKey(event.getCorrId())) {
            return true;
        }
        return false;
    }

    public void addEventState(Event event) {
        eventStateMap.put(event.getCorrId(), new EventState(event));
    }

    public void clearEventState(Event event) {
        eventStateMap.remove(event.getCorrId());
    }

    public Map<String, EventState> getEventStateMap() {
        return eventStateMap;
    }
}
