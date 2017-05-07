package no.fint.provider.events.health;

import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEventsHealth;
import no.fint.events.HealthCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class HealthCheckService implements HealthCheck<Event> {

    @Autowired
    private FintAuditService fintAuditService;

    @Autowired
    private FintEventsHealth fintEventsHealth;

    @PostConstruct
    public void init() {
        fintEventsHealth.registerServer(HealthCheckService.class);
    }

    @Override
    public Event check(Event event) {
        fintAuditService.audit(event, true);
        event.setStatus(Status.TEMP_UPSTREAM_QUEUE);
        fintAuditService.audit(event, true);
        return null;
    }
}
