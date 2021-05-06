package no.fint.provider.subscriber;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.events.FintEventListener;
import no.fint.provider.Constants;
import no.fint.provider.ProviderProps;
import no.fint.provider.eventstate.EventStateService;
import no.fint.provider.sse.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DownstreamSubscriber implements FintEventListener {

    @Autowired
    private SseService sseService;

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private FintAuditService fintAuditService;

    @Autowired
    private ProviderProps providerProps;

    @Override
    public void accept(Event event) {
        log.debug("Event received: {}", event);
        if (event.isHealthCheck()) {
            event.addObject(new Health(Constants.COMPONENT, HealthStatus.RECEIVED_IN_PROVIDER_FROM_CONSUMER));
        } else {
            eventStateService.add(event, providerProps.getStatusTtl());
        }

        sseService.send(event);
        fintAuditService.audit(event, Status.DELIVERED_TO_ADAPTER);
    }
}
