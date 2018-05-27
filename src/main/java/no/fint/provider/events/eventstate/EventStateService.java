package no.fint.provider.events.eventstate;

import com.hazelcast.core.HazelcastInstance;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.provider.events.ProviderProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class EventStateService {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private ProviderProps providerProps;

    @Getter
    private Set<EventState> eventStates;

    @PostConstruct
    public void init() {
        eventStates = hazelcastInstance.getSet(providerProps.getKey());
    }

    public void add(Event event, int timeToLiveInMinutes) {
        log.trace("Add {}, ttl={}", event, timeToLiveInMinutes);
        eventStates.add(new EventState(event, timeToLiveInMinutes));
    }

    public Optional<EventState> get(Event event) {
        Optional<EventState> result = eventStates.stream().filter(eventState -> eventState.getCorrId().equals(event.getCorrId())).findAny();
        log.trace("Get {}: {}", event.getCorrId(), result);
        return result;
    }

    public Optional<EventState> remove(Event event) {
        Optional<EventState> eventState = get(event);
        if (eventState.isPresent()) {
            boolean removed = eventStates.remove(eventState.get());
            if (!removed) {
                log.warn("Unable to remove event with corrId {} from EventStates", event.getCorrId());
            }
        }
        return eventState;
    }

}
