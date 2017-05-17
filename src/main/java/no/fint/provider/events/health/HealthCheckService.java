package no.fint.provider.events.health;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import no.fint.events.FintEventsHealth;
import no.fint.events.HealthCheck;
import no.fint.provider.events.sse.SseService;
import org.redisson.api.RBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class HealthCheckService implements HealthCheck<Event> {

    @Autowired
    private SseService sseService;

    @Autowired
    private FintAuditService fintAuditService;

    @Autowired
    private FintEventsHealth fintEventsHealth;

    @Autowired
    private FintEvents fintEvents;

    @PostConstruct
    public void init() {
        fintEventsHealth.registerServer(HealthCheckService.class);
    }

    @Override
    public Event check(Event event) {
        fintAuditService.audit(event);
        sseService.send(event);

        Event response = getEvent(event);
        fintAuditService.audit(response);
        return response;
    }

    private Event getEvent(Event event) {
        try {
            return handleReceivedEvent(event);
        } catch (InterruptedException e) {
            event.setStatus(Status.ERROR);
            event.setMessage(e.getMessage());
            return event;
        }
    }

    private Event handleReceivedEvent(Event event) throws InterruptedException {
        RBlockingQueue<Event> tempQueue = fintEvents.getTempQueue(event.getCorrId());
        Event response = tempQueue.poll(2, TimeUnit.MINUTES);
        if (response == null) {
            event.setStatus(Status.NO_RESPONSE_FOR_PROVIDER);
            event.setMessage("No response from adapter");
            return event;
        } else {
            response.setStatus(Status.TEMP_UPSTREAM_QUEUE);
            fintAuditService.audit(response);
            return response;
        }
    }
}
