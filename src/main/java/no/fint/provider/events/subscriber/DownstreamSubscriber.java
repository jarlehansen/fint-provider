package no.fint.provider.events.subscriber;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.annotations.FintEventListener;
import no.fint.provider.events.sse.SseService;
import no.fint.provider.eventstate.EventStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DownstreamSubscriber {

    @Autowired
    private SseService sseService;

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private FintAuditService fintAuditService;

    @FintEventListener
    public void receive(Event event) {
        if (eventStateService.exists(event)) {
            sseService.send(event);
        } else {
            event.setStatus(Status.DELIVERED_TO_PROVIDER);
            eventStateService.add(event);
            fintAuditService.audit(event, true);
            sseService.send(event);
        }
    }
}
