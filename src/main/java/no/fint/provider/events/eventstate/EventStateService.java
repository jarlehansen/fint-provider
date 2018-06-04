package no.fint.provider.events.eventstate;

import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.provider.events.ProviderProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventStateService {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private ProviderProps providerProps;

    private Map<String,EventState> eventStates;

    @PostConstruct
    public void init() {
        eventStates = hazelcastInstance.getMap(providerProps.getKey());
    }

    public void add(Event event, int timeToLiveInMinutes) {
        log.trace("Add {}, ttl={}", event, timeToLiveInMinutes);
        eventStates.put(event.getCorrId(), new EventState(event, timeToLiveInMinutes));
    }

    public Optional<EventState> get(Event event) {
        Optional<EventState> result = Optional.ofNullable(eventStates.get(event.getCorrId()));
        return result;
    }

    public Optional<EventState> remove(Event event) {
        Optional<EventState> eventState = Optional.ofNullable(eventStates.remove(event.getCorrId()));
        return eventState;
    }

    public List<Event> getExpiredEvents() {
        List<EventState> expired = eventStates.values().stream().filter(EventState::expired).collect(Collectors.toList());
        expired.stream().map(EventState::getCorrId).forEach(eventStates::remove);
        return expired.stream().map(EventState::getEvent).collect(Collectors.toList());
    }

    public Collection<EventState> getEventStates() {
        return eventStates.values();
    }
}
