package no.fint.provider.eventstate;

import no.fint.event.model.Event;
import no.fint.event.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class EventStateService {

    @Autowired
    private EventStateRepository eventStateRepository;

    public boolean exists(Event event) {
        return eventStateRepository.exists(event.getCorrId());
    }

    public boolean exists(Event event, Status status) {
        return eventStateRepository.exists(event.getCorrId(), status);
    }

    public void add(Event event) {
        eventStateRepository.add(new EventState(event));
    }

    public void add(String replyTo, Event event) {
        eventStateRepository.add(new EventState(replyTo, event));
    }

    public void update(Event event) {
        eventStateRepository.add(new EventState(event));
    }

    public Optional<EventState> getEventState(Event event) {
        return eventStateRepository.get(event.getCorrId());
    }

    public void clear(Event event) {
        eventStateRepository.remove(event.getCorrId());
    }

    public Map<String, EventState> getMap() {
        return eventStateRepository.getMap();
    }

}
