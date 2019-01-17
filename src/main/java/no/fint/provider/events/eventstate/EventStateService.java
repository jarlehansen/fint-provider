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
import java.util.concurrent.ConcurrentSkipListMap;
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
        if (providerProps.isUseHazelcastForEventState()) {
            eventStates = hazelcastInstance.getMap(providerProps.getKey());
        } else {
            eventStates = new ConcurrentSkipListMap<>();
        }
        log.info("Event States: {}", eventStates.getClass());
    }

    public void add(Event event, int timeToLiveInMinutes) {
        eventStates.put(event.getCorrId(), new EventState(event, timeToLiveInMinutes));
    }

    public Optional<EventState> remove(Event event) {
        return Optional.ofNullable(eventStates.remove(event.getCorrId()));
    }

    public List<Event> getExpiredEvents() {
        List<EventState> expired = eventStates.values().stream().filter(EventState::expired).collect(Collectors.toList());
        long count = expired.stream().map(EventState::getCorrId).peek(eventStates::remove).count();
        log.info("Removed {} expired events", count);
        return expired.stream().map(EventState::getEvent).collect(Collectors.toList());
    }

    public Collection<EventState> getEventStates() {
        return eventStates.values();
    }
}
