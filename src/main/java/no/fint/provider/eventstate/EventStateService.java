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
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class EventStateService {

    @Value("${fint.provider.event-state.list-name:current-corrids}")
    private String key;

    @Value("${fint.provider.event-state.timeout-in-minutes:15}")
    private int timeout;

    @Autowired
    private FintEvents fintEvents;

    @Getter
    private Set<EventState> eventStates;

    @PostConstruct
    public void init() {
        RedissonClient client = fintEvents.getClient();
        eventStates = client.getSet(key);
    }

    public boolean expired(Event event) {
        Optional<EventState> eventState = eventStates.stream().filter(e -> e.getCorrId().equals(event.getCorrId())).findAny();
        if (eventState.isPresent()) {
            Duration duration = Duration.between(Instant.ofEpochMilli(eventState.get().getTimestamp()), Instant.now());
            return (duration.toMinutes() >= timeout);
        } else {
            String status = "";
            if (event.getStatus() != null) {
                status = event.getStatus().name();
            }

            log.warn("Unable to find EventState (event action:{}, status:{})  with corrId: {}", event.getAction(), status, event.getCorrId());
            return true;
        }
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
