package no.fint.provider.events.subscriber;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import no.fint.events.annotations.FintEventListener;
import no.fint.provider.events.sse.SseService;
import no.fint.provider.eventstate.EventStateService;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DownstreamSubscriber {

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private SseService sseService;

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private FintAuditService fintAuditService;

    @FintEventListener
    public void receive(Event event) {
        event.setStatus(Status.DELIVERED_TO_PROVIDER);
        fintAuditService.audit(event, true);

        RLock lock = fintEvents.getClient().getLock(event.getCorrId());
        int counter = 0;
        while (counter < 3 && eventStateService.exists(event)) {
            counter++;
            sseService.send(event);
            lock.lock(2, TimeUnit.MINUTES);
        }
    }
}
