package no.fint.provider.events.subscriber;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.events.annotations.FintEventListener;
import no.fint.provider.events.ProviderProps;
import no.fint.provider.events.eventstate.EventStateService;
import no.fint.provider.events.sse.SseService;
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

    @Autowired
    private ProviderProps providerProps;

    @FintEventListener
    public void receive(Event event) {
        log.info("Event received: {}", event.getAction());
        if (event.isHealthCheck()) {
            event.addObject(new Health("provider", "Received in provider"));
        }

        sseService.send(event);
        event.setStatus(Status.DELIVERED_TO_PROVIDER);
        fintAuditService.audit(event);

        if (!event.isHealthCheck()) {
            eventStateService.add(event, providerProps.getStatusTtl());
        }
    }
}
