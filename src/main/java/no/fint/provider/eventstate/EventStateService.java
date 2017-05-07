package no.fint.provider.eventstate;

import lombok.Getter;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;

@Service
public class EventStateService {

    @Value("${fint.provider.event-state.list-name:current-corrids}")
    private String key;

    @Autowired
    private FintEvents fintEvents;

    @Getter
    private Set<EventState> eventStates;

    @PostConstruct
    public void init() {
        RedissonClient client = fintEvents.getClient();
        eventStates = client.getSet(key);
    }

    public boolean exists(Event event) {
        return (eventStates.contains(new EventState(event)));
    }

    public void add(Event event) {
        eventStates.add(new EventState(event));
    }

    public void remove(Event event) {
        if (exists(event)) {
            eventStates.remove(new EventState(event));
        }
    }

}
