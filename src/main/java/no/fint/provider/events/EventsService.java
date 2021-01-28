package no.fint.provider.events;

import no.fint.event.model.Event;
import no.fint.events.FintEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class EventsService implements FintEventListener {
    private final int capacity;
    private final ConcurrentMap<String, BlockingQueue<Event>> eventQueues = new ConcurrentSkipListMap<>();

    public EventsService(@Value("${fint.provider.events.capacity:100}") int capacity) {
        this.capacity = capacity;
    }

    public void register(String orgId) {
        eventQueues.computeIfAbsent(orgId, k -> new ArrayBlockingQueue<>(capacity));
    }

    public void unregister(String orgId) {
        eventQueues.remove(orgId);
    }

    @Override
    public void accept(Event event) {
        final Queue<Event> queue = eventQueues.get(event.getOrgId());
        if (queue != null) {
            queue.offer(event);
        }
    }

    public List<Event> drainEvents(String orgId) {
        List<Event> result = new ArrayList<>(capacity+10);
        final BlockingQueue<Event> queue = eventQueues.get(orgId);
        if (queue != null) {
            queue.drainTo(result);
        }
        return result;
    }

}
