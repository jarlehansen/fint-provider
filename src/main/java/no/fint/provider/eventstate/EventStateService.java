package no.fint.provider.eventstate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.Set;

@Slf4j
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

    public void add(Event event, int ttl) {
        eventStates.add(new EventState(event, ttl));
    }

    public Optional<EventState> get(Event event) {
        return eventStates.stream().filter(eventState -> eventState.getCorrId().equals(event.getCorrId())).findAny();
    }

    public void remove(Event event) {
        Optional<EventState> eventState = get(event);
        if (eventState.isPresent()) {
            boolean removed = eventStates.remove(eventState.get());
            if (!removed) {
                log.warn("Unable to remove event with corrId {} from EventStates", event.getCorrId());
            }
        }
    }

}
