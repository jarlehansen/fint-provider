package no.fint.provider.events.status;

import lombok.extern.slf4j.Slf4j;
import no.fint.audit.FintAuditService;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.events.FintEvents;
import no.fint.provider.eventstate.EventStateService;
import no.fint.provider.exceptions.UnknownEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatusService {

    @Autowired
    private EventStateService eventStateService;

    @Autowired
    private FintAuditService fintAuditService;

    @Autowired
    private FintEvents fintEvents;

    public void updateEventState(Event event) {
        if (eventStateService.exists(event)) {
            fintAuditService.audit(event, true);

            if (event.getStatus() == Status.PROVIDER_ACCEPTED) {
                eventStateService.add(event);
            } else {
                try {
                    event.setMessage(String.format("Adapter did not acknowledge the event (status: %s)", event.getStatus().name()));
                    event.setStatus(Status.UPSTREAM_QUEUE);
                    fintAuditService.audit(event, true);
                    fintEvents.sendUpstream(event.getOrgId(), event);
                    eventStateService.remove(event);
                    log.info("Adapter did not acknowledge the event (status: {}), sending event upstream.", event.getStatus().name());
                } catch (IllegalArgumentException e) {
                    log.error("Unable to create json for event object", e);
                    throw new IllegalArgumentException("Invalid Event object from adapter", e);
                }
            }
        } else {
            event.setStatus(Status.ERROR);
            fintAuditService.audit(event, true);
            throw new UnknownEventException();
        }
    }

}
