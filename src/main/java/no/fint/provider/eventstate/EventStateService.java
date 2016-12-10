package no.fint.provider.eventstate;

import no.fint.event.model.Event;
import no.fint.event.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventStateService {

    @Autowired
    private RedisRepository redisRepository;

    public boolean exists(Event event) {
        return redisRepository.exists(event.getCorrId());
    }

    public boolean exists(Event event, Status status) {
        return redisRepository.exists(event.getCorrId(), status);
    }

    public void addEventState(Event event) {
        redisRepository.add(new EventState(event));
    }

    public void addEventState(String replyTo, Event event) {
        redisRepository.add(new EventState(replyTo, event));
    }

    public Event getEvent(Event event) {
        EventState eventState = redisRepository.get(event.getCorrId());
        return eventState.getEvent();
    }

    public void updateEventState(Event event) {
        redisRepository.update(new EventState(event));
    }

    public void clearEventState(Event event) {
        redisRepository.remove(event.getCorrId());
    }

    public Map<String, EventState> getEventStateMap() {
        return redisRepository.getMap();
    }

}
