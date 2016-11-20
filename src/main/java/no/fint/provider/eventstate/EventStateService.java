package no.fint.provider.eventstate;

import no.fint.event.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventStateService {

    @Autowired
    private RedisRepository redisRepository;

    public EventStateService() {

    }

    public boolean exists(Event event) {

        if (redisRepository.exists(event.getCorrId())) {
            return true;
        }
        return false;
    }

    public void addEventState(Event event) {
        redisRepository.add(new EventState(event));
    }

    public void clearEventState(Event event) {
        redisRepository.remove(event.getCorrId());
    }


    public Map<String, EventState> getEventStateMap() {
        return redisRepository.getMap();
    }

}
