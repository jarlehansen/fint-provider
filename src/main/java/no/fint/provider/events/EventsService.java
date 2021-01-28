package no.fint.provider.events;

import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.events.FintEventListener;
import no.fint.provider.Constants;
import no.fint.provider.ProviderProps;
import no.fint.provider.eventstate.EventStateService;
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
    private final EventStateService eventStateService;
    private final FintAuditService fintAuditService;
    private final ProviderProps providerProps;

    public EventsService(
            @Value("${fint.provider.events.capacity:100}") int capacity,
            EventStateService eventStateService, FintAuditService fintAuditService, ProviderProps providerProps) {
        this.capacity = capacity;
        this.eventStateService = eventStateService;
        this.fintAuditService = fintAuditService;
        this.providerProps = providerProps;
    }

    public void register(String orgId) {
        eventQueues.computeIfAbsent(orgId, k -> new ArrayBlockingQueue<>(capacity));
    }

    public void unregister(String orgId) {
        eventQueues.remove(orgId);
    }

    @Override
    public void accept(Event event) {
        if (event.isHealthCheck()) {
            event.addObject(new Health(Constants.COMPONENT, HealthStatus.RECEIVED_IN_PROVIDER_FROM_CONSUMER));
        } else {
            eventStateService.add(event, providerProps.getStatusTtl());
        }
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
        result.forEach(event -> fintAuditService.audit(event, Status.DELIVERED_TO_ADAPTER));
        return result;
    }

}
