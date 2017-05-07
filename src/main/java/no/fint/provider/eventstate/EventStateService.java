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

    public Optional<EventState> get(String corrId) {
        return eventStateRepository.get(corrId);
    }

    public boolean exists(Event event) {
        Optional<EventState> eventState = eventStateRepository.get(event.getCorrId());
        return eventState.isPresent();
    }

    public boolean exists(Event event, Status status) {
        Optional<EventState> eventState = eventStateRepository.get(event.getCorrId());
        if (eventState.isPresent()) {
            Event e = eventState.get().getEvent();
            if (e != null) {
                return (e.getStatus() == status);
            }
        }

        return false;
    }

    public void add(Event event) {
        eventStateRepository.add(new EventState(event));
    }

    public void update(Event event) {
        eventStateRepository.get(event.getCorrId()).ifPresent(es -> eventStateRepository.add(new EventState(event)));
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
